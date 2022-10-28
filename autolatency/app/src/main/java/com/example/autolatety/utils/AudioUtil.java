package com.example.autolatety.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.autolatety.data.FileSound;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AudioUtil {
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int SAMPLE_RATE = 8000; // Hz
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int CHANNEL_MASK = AudioFormat.CHANNEL_IN_MONO;
    private static final int BUFFER_SIZE = 2 * AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_MASK, ENCODING);

    private static final String TAG = "AudioUtil";
    private static AudioRecord audioRecorder = null;

    private static ProgressListener mProgressListener = null;
    private static final String dir = "/Auio test";
    private static MediaRecorder recorder = null;
    private static MediaPlayer player = null;
    private static FileSound fileSound;
    public static boolean isCancel = false;

    public static void startPlaying(String file) {
        player = new MediaPlayer();
        try {
            player.setDataSource(file);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e("LOG_TAG", "prepare() failed");
        }
    }

    public static void stopPlaying() {
        player.release();
        player = null;

    }

    public static void startRecordingByMR() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File rootPath = new File(Environment.getExternalStorageDirectory(), dir);
                if (!rootPath.exists()) {
                    rootPath.mkdirs();
                }
                String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + dir;
                fileName += "/" + TimeUtil.getStringTime() + ".3gp";
                recorder = new MediaRecorder();


                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setOutputFile(fileName);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                try {
                    recorder.prepare();
                } catch (IOException e) {
                    Log.e("LOG_TAG", e.getMessage());
                }

                recorder.start();
            }
        }).start();
    }

    public static void stopRecordingByMR() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    public static void startRecordingByAR(Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                isCancel = false;
                audioRecorder = null;
                FileOutputStream wavOut = null;
                File rootPath = new File(Environment.getExternalStorageDirectory(), dir);
                if (!rootPath.exists()) {
                    rootPath.mkdirs();
                }
                String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + dir;
                fileName += "/" + TimeUtil.getStringTime() + ".wav";
                File file = new File(fileName);
                long startTime = 0;
                long endTime = 0;
                try {
                    // Open our two resources
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    audioRecorder = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_MASK, ENCODING, BUFFER_SIZE);
                       wavOut = new FileOutputStream(file);

                       // Write out the wav file header
                       writeWavHeader(wavOut, CHANNEL_MASK, SAMPLE_RATE, ENCODING);

                       // Avoiding loop allocations
                       byte[] buffer = new byte[BUFFER_SIZE];
                       boolean run = true;
                       int read;
                       long total = 0;

                       // Let's go
                       startTime = SystemClock.elapsedRealtime();
                    audioRecorder.startRecording();
                       while (run && !isCancelled()) {
                           read = audioRecorder.read(buffer, 0, buffer.length);

                           // WAVs cannot be > 4 GB due to the use of 32 bit unsigned integers.
                           if (total + read > 4294967295L) {
                               // Write as many bytes as we can before hitting the max size
                               for (int i = 0; i < read && total <= 4294967295L; i++, total++) {
                                   wavOut.write(buffer[i]);
                               }
                               run = false;
                           } else {
                               // Write out the entire read buffer
                               wavOut.write(buffer, 0, read);
                               total += read;
                           }
                       }
                   } catch (IOException ex) {
                       return ;
                   } finally {
                       if (audioRecorder != null) {
                           try {
                               if (audioRecorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                                   audioRecorder.stop();
                                   endTime = SystemClock.elapsedRealtime();
                               }
                           } catch (IllegalStateException ex) {
                               //
                           }
                           if (audioRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
                               audioRecorder.release();
                           }
                       }
                       if (wavOut != null) {
                           try {
                               wavOut.close();
                           } catch (IOException ex) {
                               //
                           }
                       }
                   }

                   try {
                       // This is not put in the try/catch/finally above since it needs to run
                       // after we close the FileOutputStream
                       updateWavHeader(file);
                   } catch (IOException ex) {
                       return ;
                   }

                   return ;
               }
        }).start();
    }

    private static boolean isCancelled() {
        return isCancel;
    }

    public static void cancel (){
        isCancel = true;
    }

    private static void updateWavHeader(File wav) throws IOException {
        byte[] sizes = ByteBuffer
                .allocate(8)
                .order(ByteOrder.LITTLE_ENDIAN)
                // There are probably a bunch of different/better ways to calculate
                // these two given your circumstances. Cast should be safe since if the WAV is
                // > 4 GB we've already made a terrible mistake.
                .putInt((int) (wav.length() - 8)) // ChunkSize
                .putInt((int) (wav.length() - 44)) // Subchunk2Size
                .array();

        RandomAccessFile accessWave = null;
        //noinspection CaughtExceptionImmediatelyRethrown
        try {
            accessWave = new RandomAccessFile(wav, "rw");
            // ChunkSize
            accessWave.seek(4);
            accessWave.write(sizes, 0, 4);

            // Subchunk2Size
            accessWave.seek(40);
            accessWave.write(sizes, 4, 4);
        } catch (IOException ex) {
            // Rethrow but we still close accessWave in our finally
            throw ex;
        } finally {
            if (accessWave != null) {
                try {
                    accessWave.close();
                } catch (IOException ex) {
                    //
                }
            }
        }
    }

    private static void writeWavHeader(OutputStream out, int channelMask, int sampleRate, int encoding) throws IOException {
        short channels;
        switch (channelMask) {
            case AudioFormat.CHANNEL_IN_MONO:
                channels = 1;
                break;
            case AudioFormat.CHANNEL_IN_STEREO:
                channels = 2;
                break;
            default:
                throw new IllegalArgumentException("Unacceptable channel mask");
        }

        short bitDepth;
        switch (encoding) {
            case AudioFormat.ENCODING_PCM_8BIT:
                bitDepth = 8;
                break;
            case AudioFormat.ENCODING_PCM_16BIT:
                bitDepth = 16;
                break;
            case AudioFormat.ENCODING_PCM_FLOAT:
                bitDepth = 32;
                break;
            default:
                throw new IllegalArgumentException("Unacceptable encoding");
        }

        writeWavHeader(out, channels, sampleRate, bitDepth);
    }

    private static void writeWavHeader(OutputStream out, short channels, int sampleRate, short bitDepth) throws IOException {
        // Convert the multi-byte integers to raw bytes in little endian format as required by the spec
        byte[] littleBytes = ByteBuffer
                .allocate(14)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putShort(channels)
                .putInt(sampleRate)
                .putInt(sampleRate * channels * (bitDepth / 8))
                .putShort((short) (channels * (bitDepth / 8)))
                .putShort(bitDepth)
                .array();

        // Not necessarily the best, but it's very easy to visualize this way
        out.write(new byte[]{
                // RIFF header
                'R', 'I', 'F', 'F', // ChunkID
                0, 0, 0, 0, // ChunkSize (must be updated later)
                'W', 'A', 'V', 'E', // Format
                // fmt subchunk
                'f', 'm', 't', ' ', // Subchunk1ID
                16, 0, 0, 0, // Subchunk1Size
                1, 0, // AudioFormat
                littleBytes[0], littleBytes[1], // NumChannels
                littleBytes[2], littleBytes[3], littleBytes[4], littleBytes[5], // SampleRate
                littleBytes[6], littleBytes[7], littleBytes[8], littleBytes[9], // ByteRate
                littleBytes[10], littleBytes[11], // BlockAlign
                littleBytes[12], littleBytes[13], // BitsPerSample
                // data subchunk
                'd', 'a', 't', 'a', // Subchunk2ID
                0, 0, 0, 0, // Subchunk2Size (must be updated later)
        });
    }

    public static void stopRecordingByAR() {
        if (null != audioRecorder) {
            audioRecorder.stop();
            audioRecorder.release();
            audioRecorder = null;
            isCancel = true;
        }
    }

    private void setProgressListener(ProgressListener progressListener) {
        mProgressListener = progressListener;
    }

    public static void pausePlaying() {
        recorder.stop();
        recorder.release();
        recorder = null;

    }

    public static void readAudioFile(String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File inputFile = new File(path);
                if (!inputFile.exists()) {
                    try {
                        throw new FileNotFoundException(path);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                fileSound = new FileSound();
                MediaExtractor extractor = new MediaExtractor();
                MediaFormat format = null;
                int i;

                String mFileType ="3gp";
                fileSound.setmFileType("3gp");
                int  mFileSize = (int)inputFile.length();
                try {
                    extractor.setDataSource(inputFile.getPath());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                int numTracks = extractor.getTrackCount();
                for (i=0; i<numTracks; i++) {
                    format = extractor.getTrackFormat(i);
                    if (format.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
                        extractor.selectTrack(i);
                        break;
                    }
                }
                if (i == numTracks) {
                    Log.d("TAG","No audio track found in " + inputFile);
                    return;
                }
                fileSound.setmChannels(format.getInteger(MediaFormat.KEY_CHANNEL_COUNT));
                fileSound.setmSampleRate( format.getInteger(MediaFormat.KEY_SAMPLE_RATE));
                Log.d("TAG", "SampleRate: "+ fileSound.getmSampleRate()+" chanels: "+fileSound.getmChannels());
                // Expected total number of samples per channel.
                int expectedNumSamples =
                        (int)((format.getLong(MediaFormat.KEY_DURATION) / 1000000.f) * fileSound.getmSampleRate() + 0.5f);

                MediaCodec codec = null;
                try {
                    codec = MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME));
                    codec.configure(format, null, null, 0);
                    codec.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int decodedSamplesSize = 0;  // size of the output buffer containing decoded samples.
                byte[] decodedSamples = null;
                ByteBuffer[] inputBuffers = codec.getInputBuffers();
                ByteBuffer[] outputBuffers = codec.getOutputBuffers();
                int sample_size;
                MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                long presentation_time;
                int tot_size_read = 0;
                boolean done_reading = false;

                fileSound.setmDecodedBytes( ByteBuffer.allocate(1<<20));
                Boolean firstSampleData = true;
                while (true) {
                    // read data from file and feed it to the decoder input buffers.
                    int inputBufferIndex = codec.dequeueInputBuffer(100);
                    if (!done_reading && inputBufferIndex >= 0) {
                        sample_size = extractor.readSampleData(inputBuffers[inputBufferIndex], 0);
                        if (firstSampleData
                                && format.getString(MediaFormat.KEY_MIME).equals("audio/mp4a-latm")
                                && sample_size == 2) {
                            extractor.advance();
                            tot_size_read += sample_size;
                        } else if (sample_size < 0) {
                            // All samples have been read.
                            codec.queueInputBuffer(
                                    inputBufferIndex, 0, 0, -1, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            done_reading = true;
                        } else {
                            presentation_time = extractor.getSampleTime();
                            codec.queueInputBuffer(inputBufferIndex, 0, sample_size, presentation_time, 0);
                            extractor.advance();
                            tot_size_read += sample_size;
                            if (mProgressListener != null) {
                                if (!mProgressListener.reportProgress((float)(tot_size_read) / mFileSize)) {
                                    extractor.release();
                                    extractor = null;
                                    codec.stop();
                                    codec.release();
                                    codec = null;
                                    return;
                                }
                            }
                        }
                        firstSampleData = false;
                    }

                    // Get decoded stream from the decoder output buffers.
                    int outputBufferIndex = codec.dequeueOutputBuffer(info, 100);
                    if (outputBufferIndex >= 0 && info.size > 0) {
                        if (decodedSamplesSize < info.size) {
                            decodedSamplesSize = info.size;
                            decodedSamples = new byte[decodedSamplesSize];
                        }
                        outputBuffers[outputBufferIndex].get(decodedSamples, 0, info.size);
                        outputBuffers[outputBufferIndex].clear();
                        // Check if buffer is big enough. Resize it if it's too small.
                        if (fileSound.getmDecodedBytes().remaining() < info.size) {
                            // Getting a rough estimate of the total size, allocate 20% more, and
                            // make sure to allocate at least 5MB more than the initial size.
                            int position = fileSound.getmDecodedBytes().position();
                            int newSize = (int)((position * (1.0 * mFileSize / tot_size_read)) * 1.2);
                            if (newSize - position < info.size + 5 * (1<<20)) {
                                newSize = position + info.size + 5 * (1<<20);
                            }
                            ByteBuffer newDecodedBytes = null;
                            // Try to allocate memory. If we are OOM, try to run the garbage collector.
                            int retry = 10;
                            while(retry > 0) {
                                try {
                                    newDecodedBytes = ByteBuffer.allocate(newSize);
                                    break;
                                } catch (OutOfMemoryError oome) {

                                    retry--;
                                }
                            }
                            if (retry == 0) {
                                break;
                            }
                            //ByteBuffer newDecodedBytes = ByteBuffer.allocate(newSize);
                            fileSound.getmDecodedBytes().rewind();
                            newDecodedBytes.put(fileSound.getmDecodedBytes());
                            fileSound.setmDecodedBytes(newDecodedBytes);
                            fileSound.getmDecodedBytes().position(position);
                        }
                        fileSound.getmDecodedBytes().put(decodedSamples, 0, info.size);
                        codec.releaseOutputBuffer(outputBufferIndex, false);
                    } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        outputBuffers = codec.getOutputBuffers();
                    } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {

                    }
                    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0
                            || (fileSound.getmDecodedBytes().position() / (2 * fileSound.getmChannels())) >= expectedNumSamples) {
                        break;
                    }
                }
                fileSound.setmNumSamples(fileSound.getmDecodedBytes().position() / (fileSound.getmChannels() * 2));  // One sample = 2 bytes.
                fileSound.getmDecodedBytes().rewind();
                fileSound.getmDecodedBytes().order(ByteOrder.LITTLE_ENDIAN);
                fileSound.setmDecodedSamples( fileSound.getmDecodedBytes().asShortBuffer());
                fileSound.setmAvgBitRate((int)((mFileSize * 8) * ((float)fileSound.getmSampleRate() / fileSound.getmNumSamples()) / 1000));

                extractor.release();
                extractor = null;
                codec.stop();
                codec.release();
                codec = null;

                // Temporary hack to make it work with the old version.
                fileSound.setmNumFrames (fileSound.getmNumSamples() /1024);
                if (fileSound.getmNumSamples() % 1024 != 0){
                    fileSound.setmNumFrames(fileSound.getmNumFrames()+1);
                }
                fileSound.setmFrameGains(new int[fileSound.getmNumFrames()]);
                fileSound.setmFrameLens (new int[fileSound.getmNumFrames()]);
                fileSound.setmFrameOffsets ( new int[fileSound.getmNumFrames()]);
                int j;
                int gain, value;
                int frameLens = (int)((1000 * fileSound.getmAvgBitRate() / 8) *
                        ((float)1024 / fileSound.getmSampleRate()));
                for (i=0; i<fileSound.getmNumFrames(); i++){
                    gain = -1;
                    for(j=0; j<1024; j++) {
                        value = 0;
                        for (int k=0; k<fileSound.getmChannels(); k++) {
                            if (fileSound.getmDecodedSamples().remaining() > 0) {
                                value += Math.abs(fileSound.getmDecodedSamples().get());
                            }
                        }
                        value /= fileSound.getmChannels();
                        if (gain < value) {
                            gain = value;
                        }
                    }
                    fileSound.getmFrameGains()[i] = (int)Math.sqrt(gain);  // here gain = sqrt(max value of 1st channel)...
                    fileSound.getmFrameLens()[i] = frameLens;  // totally not accurate...
                    fileSound.getmFrameOffsets()[i] = (int)(i * (1000 * fileSound.getmAvgBitRate() / 8) *  //  = i * frameLens
                            ((float)1024 / fileSound.getmSampleRate()));
                }
                fileSound.getmDecodedSamples().rewind();
                // DumpSamples();  // Uncomment this line to dump the samples in a TSV file.
                Log.d("TAG", "readAudioFile: "+fileSound.getmNumSamples());
            }
        }).start();
    }

    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        final int bufLen = 4 * 0x400; // 4KB
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;

        try {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
                    outputStream.write(buf, 0, readLen);

                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            if (exception == null) inputStream.close();
            else try {
                inputStream.close();
            } catch (IOException e) {
                exception.addSuppressed(e);
            }
        }
    }

    public static void recordFromURI(){

    }

    public static void playFromUri(Uri uri,Context context){
        player = new MediaPlayer();

        try {
            // mediaPlayer.setDataSource(String.valueOf(myUri));
            player.setDataSource(context,uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.start();
    }
    // Progress listener interface.
    public interface ProgressListener {
        boolean reportProgress(double fractionComplete);
    }
}

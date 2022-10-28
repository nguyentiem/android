package com.example.record_wav.data;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class FileSound {

    private File mInputFile = null;

    // Member variables representing frame data
    private String mFileType;
    private int mFileSize;
    private int mAvgBitRate;  // Average bit rate in kbps.
    private int mSampleRate;
    private int mChannels;
    private int mNumSamples;  // total number of samples per channel in audio file
    private ByteBuffer mDecodedBytes;  // Raw audio data
    private ShortBuffer mDecodedSamples;  // shared buffer with mDecodedBytes.


    // Member variables for hack (making it work with old version, until app just uses the samples).
    private int mNumFrames;
    private int[] mFrameGains;
    private int[] mFrameLens;
    private int[] mFrameOffsets;

    public File getmInputFile() {
        return mInputFile;
    }

    public void setmInputFile(File mInputFile) {
        this.mInputFile = mInputFile;
    }

    public String getmFileType() {
        return mFileType;
    }

    public void setmFileType(String mFileType) {
        this.mFileType = mFileType;
    }

    public int getmFileSize() {
        return mFileSize;
    }

    public void setmFileSize(int mFileSize) {
        this.mFileSize = mFileSize;
    }

    public int getmAvgBitRate() {
        return mAvgBitRate;
    }

    public void setmAvgBitRate(int mAvgBitRate) {
        this.mAvgBitRate = mAvgBitRate;
    }

    public int getmSampleRate() {
        return mSampleRate;
    }

    public void setmSampleRate(int mSampleRate) {
        this.mSampleRate = mSampleRate;
    }

    public int getmChannels() {
        return mChannels;
    }

    public void setmChannels(int mChannels) {
        this.mChannels = mChannels;
    }

    public int getmNumSamples() {
        return mNumSamples;
    }

    public void setmNumSamples(int mNumSamples) {
        this.mNumSamples = mNumSamples;
    }

    public ByteBuffer getmDecodedBytes() {
        return mDecodedBytes;
    }

    public void setmDecodedBytes(ByteBuffer mDecodedBytes) {
        this.mDecodedBytes = mDecodedBytes;
    }

    public ShortBuffer getmDecodedSamples() {
        return mDecodedSamples;
    }

    public void setmDecodedSamples(ShortBuffer mDecodedSamples) {
        this.mDecodedSamples = mDecodedSamples;
    }

    public int getmNumFrames() {
        return mNumFrames;
    }

    public void setmNumFrames(int mNumFrames) {
        this.mNumFrames = mNumFrames;
    }

    public int[] getmFrameGains() {
        return mFrameGains;
    }

    public void setmFrameGains(int[] mFrameGains) {
        this.mFrameGains = mFrameGains;
    }

    public int[] getmFrameLens() {
        return mFrameLens;
    }

    public void setmFrameLens(int[] mFrameLens) {
        this.mFrameLens = mFrameLens;
    }

    public int[] getmFrameOffsets() {
        return mFrameOffsets;
    }

    public void setmFrameOffsets(int[] mFrameOffsets) {
        this.mFrameOffsets = mFrameOffsets;
    }

    // Should be removed when the app will use directly the samples instead of the frames.
    public int getSamplesPerFrame() {
        return 1024;  // just a fixed value here...
    }

    // Should be removed when the app will use directly the samples instead of the frames.
    public int[] getFrameGains() {
        return mFrameGains;
    }

}


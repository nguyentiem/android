package com.pdfreader.scanner.pdfviewer.utils.pdf;

import android.content.Context;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.exceptions.BadPasswordException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.constants.AppConstants;
import com.pdfreader.scanner.pdfviewer.constants.DataConstants;
import com.pdfreader.scanner.pdfviewer.utils.file.DirectoryUtils;
import com.pdfreader.scanner.pdfviewer.utils.file.FileUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class PdfUtils {
    private static final int DEFAULT_DISTANCE = 50;

    public static String setPasswordPdfFile(Context context, String path, String password) throws IOException, DocumentException {

        String appPassword = AppConstants.APP_PASSWORD;
        String fileName = FileUtils.getFileName(path);
        String newPath = DirectoryUtils.getDefaultStorageLocation() + "/" + fileName;

        String finalOutputFile = FileUtils.getUniquePdfFileName(context, FileUtils.getLastReplacePath(newPath, DataConstants.PDF_EXTENSION,
                context.getString(R.string.protect_pdf_encrypted_file_name)));

        PdfReader reader = new PdfReader(path);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(finalOutputFile));
        stamper.setEncryption(password.getBytes(), appPassword.getBytes(),
                PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY, PdfWriter.ENCRYPTION_AES_128);
        stamper.close();
        reader.close();
        return finalOutputFile;
    }

    public static String removePassword(Context context, String path, String inputPassword) {
        String finalOutputFile;
        String fileName = FileUtils.getFileName(path);
        String newPath = DirectoryUtils.getDefaultStorageLocation() + "/" + fileName;

        finalOutputFile = FileUtils.getUniquePdfFileName(context, FileUtils.getLastReplacePath(newPath, DataConstants.PDF_EXTENSION,
                context.getString(R.string.unlock_pdf_encrypted_file_name)));

        if (!removePasswordUsingDefMasterPassword(path, finalOutputFile, inputPassword)) {
            if (!removePasswordUsingInputMasterPassword(path, finalOutputFile, inputPassword)) {
                return null;
            }
        }

        return finalOutputFile;
    }

    private static boolean removePasswordUsingDefMasterPassword(final String path,
                                                                final String finalOutputFile,
                                                                final String inputPassword) {
        try {
            String masterPwd = AppConstants.APP_PASSWORD;
            PdfReader reader = new PdfReader(path, masterPwd.getBytes());
            byte[] password;

            password = reader.computeUserPassword();
            byte[] input = inputPassword.getBytes();
            if (Arrays.equals(input, password)) {
                PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(finalOutputFile));
                stamper.close();
                reader.close();

                return true;
            }
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static boolean removePasswordUsingInputMasterPassword(final String file,
                                                                  final String finalOutputFile,
                                                                  final String inputPassword) {
        try {
            PdfReader reader = new PdfReader(file, inputPassword.getBytes());
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(finalOutputFile));
            stamper.close();
            reader.close();

            return true;

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isPDFEncrypted(String path) {
        boolean isEncrypted;
        PdfReader pdfReader = null;
        try {
            pdfReader = new PdfReader(path);
            isEncrypted = pdfReader.isEncrypted();
        } catch (BadPasswordException e) {
            isEncrypted = true;
        } catch (IOException e) {
            isEncrypted = false;
        } finally {
            if (pdfReader != null) pdfReader.close();
        }
        return isEncrypted;
    }

    public static boolean isPasswordValid(String filePath, byte[] password) {
        try {
            PdfReader pdfReader = new PdfReader(filePath, password);
            return true;
        } catch (BadPasswordException e) {
            return  false;
        } catch (IOException e) {
            return true;
        }
    }
}

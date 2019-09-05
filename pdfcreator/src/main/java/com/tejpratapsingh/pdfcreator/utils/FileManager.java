package com.tejpratapsingh.pdfcreator.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Admin on 02-01-2017.
 */
public class FileManager {

    public enum FILE_TYPE implements Serializable {
        IMAGE, AUDIO, VIDEO, PDF, TEXT, DOC, PPT, XLS, UNKNOWN
    }

    public enum IMAGE_QUALITY implements Serializable {
        HIGH(1024), MID(512), LOW(256);

        private int size;

        IMAGE_QUALITY(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }

    private final String tempDirectoryName = "temp";
    private final String tempDuplicateFileNameSuffix = "dup";
    private final String DOCUMENT_THUMBNAIL_SUFFIX = "-thumb.jpeg";
    private final int IMAGE_DOCUMENT_MAX_THUMBNAIL_SIZE = 50; // 50KB

    private static FileManager instance;

    private FileManager() {
    }

    public static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }

    /**
     * Write file from InputStream, file will be overwritten if exist
     *
     * @param context          context
     * @param inputStream      input stream reader of data (file)
     * @param fileName         name of file which has to saved
     * @param overWriteIfExist if true, file will be overwritten if exist
     * @return saved file
     * @throws IOException
     */
    public File saveFileToPrivateStorageFromInputStream(Context context, InputStream inputStream, String fileName, boolean overWriteIfExist, boolean createThumbnail) throws Exception, IOException {
        // Create file directory if not exist
        makeDirectoryInPrivateStorage(context, fileName.substring(0, fileName.indexOf("/")));
        // Start referencing a new file
        File fileToSave = new File(context.getExternalFilesDir(null), fileName);
        if (overWriteIfExist == false) {
            // Check if file already exist or not, return if exist
            boolean isFileAlreadyExist = hasExternalStoragePrivateFile(context, fileName);
            if (isFileAlreadyExist) {
                throw new Exception("File Already Exists, make it overWritable to replace.");
            }
        }
        FileOutputStream fileOutput = new FileOutputStream(fileToSave);

        byte[] buffer = new byte[1024];
        int bufferLength = 0;

        while ((bufferLength = inputStream.read(buffer)) > 0) {
            fileOutput.write(buffer, 0, bufferLength);
        }
        fileOutput.flush();
        fileOutput.close();
        inputStream.close();
        String fileMimeType = getMimeType(context, fileToSave);
        if (fileMimeType == null) {
            fileMimeType = "";
        }
        if (createThumbnail && fileMimeType.split("/")[0].equals("image")) {
            // Create thumbnail
            try {
                File thumbNailFile = new File(fileToSave.getAbsolutePath() + DOCUMENT_THUMBNAIL_SUFFIX);
                if (thumbNailFile.exists() == false) {
                    // Create if not already exist
                    Bitmap thumbnailBitmap = crateThumbnail(fileToSave, IMAGE_DOCUMENT_MAX_THUMBNAIL_SIZE);
                    OutputStream out = new FileOutputStream(thumbNailFile);
                    thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                // Unable to create file, likely because external storage is
                // not currently mounted.
                Log.e("ExternalStorage", "Error writing thumbnail");
                throw e;
            }
        }
        return fileToSave;
    }

    /**
     * Save a string in file
     *
     * @param fileToWrite
     * @param dataToWrite
     * @return
     * @throws Exception
     */
    public File saveStringToFile(File fileToWrite, String dataToWrite) throws Exception {
        try {
            FileWriter fw = new FileWriter(fileToWrite);
            fw.write(dataToWrite);
            fw.close();
            return fileToWrite;
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Delete file in external private storage
     *
     * @param context  context
     * @param fileName name of file in external private storage to be deleted
     * @return boolean is file deleted successfully or not
     * @throws Exception
     */
    public boolean deleteExternalStoragePrivateFile(Context context, String fileName) throws Exception {
        // Get path for the file on external storage.
        // If external storage is not currently mounted this will fail.
        File file = new File(context.getExternalFilesDir(null), fileName);
        if (file != null) {
            boolean fileDeleted = file.delete();

            File thumbFile = new File(file.getAbsolutePath() + DOCUMENT_THUMBNAIL_SUFFIX);
            if (fileDeleted && thumbFile.exists()) {
                thumbFile.delete();
            }
            return true;
        } else {
            throw new Exception("File does not exist.");
        }
    }

    /**
     * Check if file is available in private storage
     *
     * @param context  context
     * @param fileName Name of file in external private storage to be checked
     * @return boolean is file exist or not
     */
    public boolean hasExternalStoragePrivateFile(Context context, String fileName) {
        // Get path for the file on external storage.
        // If external storage is not currently mounted this will fail.
        File file = new File(context.getExternalFilesDir(null), fileName);
        if (file != null) {
            return file.exists();
        }
        return false;
    }

    /**
     * Get list of all files in external Storage
     *
     * @param context context
     * @return list of files
     */
    public File[] listExternalStoragePrivateFile(Context context) {
        String path = context.getExternalFilesDir(null) + "";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        return directory.listFiles();
    }

    /**
     * Check if file exists or not and return its url
     *
     * @param context  context
     * @param fileName name of file
     * @return path of file
     * @throws Exception
     */
    public String getFileUrlFromExternalStoragePrivateFile(Context context, String fileName) throws Exception {
        if (hasExternalStoragePrivateFile(context, fileName)) {
            return context.getExternalFilesDir(null) + File.separator + fileName;
        }
        throw new Exception("No File Found");
    }

    /**
     * Resize image to specific size
     *
     * @param file file which has to be resized
     * @param size new size to scale to
     * @return resized image bitmap
     * @throws Exception
     */
    public Bitmap crateThumbnail(File file, int size) throws Exception {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), o);
            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= size && o.outHeight / scale / 2 >= size)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(file.getAbsolutePath(), o2);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Check if thumbnail file exists or not and return its url
     *
     * @param context  context
     * @param fileName name of file whose thumbnail has to be given
     * @return path of file
     * @throws Exception
     */
    public String getThumbnailFileUrlFromExternalStoragePrivateFile(Context context, String fileName) throws Exception {
        if (hasExternalStoragePrivateFile(context, fileName + DOCUMENT_THUMBNAIL_SUFFIX)) {
            return context.getExternalFilesDir(null) + File.separator + fileName + DOCUMENT_THUMBNAIL_SUFFIX;
        }
        throw new Exception("No File Found");
    }

    /**
     * Get Mime type from a file
     *
     * @param file file whose mime type has to find
     * @return mime type of string
     */
    public String getMimeType(Context context, @NonNull File file) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        if (type == null) {
            try {
                type = file.toURI().toURL().openConnection().getContentType();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (type == null) {
            type = context.getContentResolver().getType(Uri.fromFile(file));
        }
        if (type == null) {
            type = URLConnection.guessContentTypeFromName(file.getName());
        }
        if (type == null) {
            // If nothing worked, just set mime type to empty string
            type = "";
        }
        return type;
    }

    /**
     * Gte file's extension without "."
     *
     * @param sourceFile whole extension has to be found
     * @return file extension without "."
     */
    public String getFileExtension(File sourceFile) {
        if (sourceFile == null || sourceFile.getName().lastIndexOf(".") <= 0) {
            return null;
        }
        String[] fileNameParts = sourceFile.getName().split("\\.");
        Log.d("AddNewDocumentActivity", "fileNameParts.length: " + fileNameParts.length);
        Log.d("AddNewDocumentActivity", "getFileExtension: " + fileNameParts[fileNameParts.length - 1]);
        return fileNameParts[fileNameParts.length - 1];
    }

    public File getFileFromURI(Context context, Uri contentUri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(contentUri);
            File fileToSave = createTempFile(context, getFileExtension(new File(getFileName(context, contentUri))), false);
            FileOutputStream fileOutput = new FileOutputStream(fileToSave);

            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
            }
            fileOutput.flush();
            fileOutput.close();
            inputStream.close();
            return fileToSave;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /**
     * Generates a random path for file within 0 - 9
     *
     * @param context current context
     * @return random directory number
     * @throws SecurityException
     */
    private int getRandomFileDirectory(Context context) throws SecurityException {
        int randomDirectoryName = Utilities.generateRandomNumber(0, 9);
        makeDirectoryInPrivateStorage(context, String.valueOf(randomDirectoryName));
        return randomDirectoryName;
    }

    private boolean makeDirectoryInPrivateStorage(Context context, String directoryName) {
        File randomDirectory = new File(context.getExternalFilesDir(null) + File.separator + directoryName);
        if (!randomDirectory.exists()) {
            System.out.println("creating directory: " + directoryName);
            randomDirectory.mkdir();
        }
        return true;
    }

    /**
     * Get Temp folder from private storage (create one if not exist)
     *
     * @param context current context
     * @return temp folder location (path)
     */
    public String getTempFolder(Context context) {
        File tempDirectory = new File(context.getExternalFilesDir(null) + File.separator + tempDirectoryName);
        if (!tempDirectory.exists()) {
            System.out.println("creating directory: temp");
            tempDirectory.mkdir();
        }

        return tempDirectory.getAbsolutePath();
    }

    /**
     * Create a temporary file in temp folder
     *
     * @param context       current context
     * @param withExtension specify file extension
     * @param withDuplicate create a duplicate of temp file (used in case of image processing), DUPLICATE FILES ARE JUST A EMPTY FILE PATH, ALL FILE MANAGEMENT HAS TO BE DONE BY YOU.
     * @return created temp file
     */
    public File createTempFile(Context context, String withExtension, boolean withDuplicate) {
        // Actual temp file
        String tempFileName = Long.toString(new Date().getTime());
        if (withExtension != null && withExtension.isEmpty() == false) {
            tempFileName = tempFileName + "." + withExtension;
        }
        File tempFile = new File(getTempFolder(context), tempFileName);
        if (withDuplicate) {
            // Duplicate of temp file
            File tempDuplicateFile = new File(getTempFolder(context), tempFileName + tempDuplicateFileNameSuffix);
        }

        return tempFile;
    }

    /**
     * Create a temporary file in temp folder with user given name, file will be overwritten if name is collapsed
     *
     * @param context       current context
     * @param withDuplicate create a duplicate of temp file (used in case of image processing), DUPLICATE FILES ARE JUST A EMPTY FILE PATH, ALL FILE MANAGEMENT HAS TO BE DONE BY YOU.
     * @return created temp file
     */
    public File createTempFileWithName(Context context, String tempFileName, boolean withDuplicate) {
        // Actual temp file
        File tempFile = new File(getTempFolder(context), tempFileName);
        if (withDuplicate) {
            // Duplicate of temp file
            File tempDuplicateFile = new File(getTempFolder(context), tempFileName + tempDuplicateFileNameSuffix);
        }

        return tempFile;
    }

    /**
     * Save a bitmap to a file
     *
     * @param fileToSave      File object to save file to
     * @param bitmapToSave    Bitmap to be saved
     * @param createThumbnail true if you want to create thumbnail as well
     * @return saved file
     * @throws Exception
     * @throws IOException
     */
    public File saveImageToFile(File fileToSave, Bitmap bitmapToSave, Bitmap.CompressFormat compressFormat, boolean createThumbnail) throws Exception, IOException {
        try {
            // If external storage is not currently mounted this will fail.
            OutputStream out = new FileOutputStream(fileToSave);
            bitmapToSave.compress(compressFormat, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.e("ExternalStorage", "Error writing " + fileToSave, e);
            throw new IOException("Error writing " + fileToSave + ", Exception: " + e.getMessage());
        }

        if (createThumbnail) {
            try {
                File thumbNailFile = new File(fileToSave.getAbsolutePath() + DOCUMENT_THUMBNAIL_SUFFIX);
                Bitmap thumbnailBitmap = crateThumbnail(fileToSave, IMAGE_DOCUMENT_MAX_THUMBNAIL_SIZE);
                OutputStream out = new FileOutputStream(thumbNailFile);
                thumbnailBitmap.compress(compressFormat, 100, out);
                out.flush();
                out.close();
            } catch (IOException e) {
                // Unable to create file, likely because external storage is
                // not currently mounted.
                Log.e("ExternalStorage", "Error writing thumbnail");
                throw e;
            }
        }
        return fileToSave;
    }

    /**
     * Get temporary file by its name
     *
     * @param context  current context
     * @param fileName name if temp file
     * @return file if found, null if not
     */
    public File getTempFile(Context context, String fileName) throws Exception {
        File tempFile = new File(getTempFolder(context), fileName);
        if (tempFile.exists() == false) {
            throw new Exception("File not found");
        }
        return tempFile;
    }

    /**
     * Get duplicate of temporary file by its temp file name
     *
     * @param context  current context
     * @param fileName name if temp file
     * @return file if found, null if not
     */
    public File getTempDuplicateFile(Context context, String fileName) throws Exception {
        File tempDuplicateFile = new File(getTempFolder(context), fileName + tempDuplicateFileNameSuffix);
        if (tempDuplicateFile.exists() == false) {
            throw new Exception("File not found");
        }
        return tempDuplicateFile;
    }

    /**
     * Delete all files from temp directory
     *
     * @param context current context
     */
    public void cleanTempFolder(Context context) {
        File tempDirectory = new File(context.getExternalFilesDir(null) + File.separator + tempDirectoryName);
        if (tempDirectory.exists()) {
            try {
                for (File f : tempDirectory.listFiles())
                    f.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get Available storage of external storage
     *
     * @return size in bytes
     */
    public long getAvailableStorageSpace() {
        return Environment.getExternalStorageDirectory().getFreeSpace();
    }

    /**
     * Get Total Space of external storage
     *
     * @return size in bytes
     */
    public long getTotalStorageSpace() {
        return Environment.getExternalStorageDirectory().getTotalSpace();
    }

    /**
     * Get human readable file size
     *
     * @param fileSizeInKb size of file in kb
     * @return human readable file
     */
    public String getReadableFileSize(long fileSizeInKb) {
        NumberFormat numberFormat = new DecimalFormat("##.##");
        if (fileSizeInKb < 1024) {
            return String.format(Locale.getDefault(), "%s KB", numberFormat.format((float) fileSizeInKb));
        } else if (fileSizeInKb >= 1024 && fileSizeInKb < (1024 * 1024)) {
            return String.format(Locale.getDefault(), "%s MB", numberFormat.format((float) fileSizeInKb / 1024));
        } else {
            return String.format(Locale.getDefault(), "%s GB", numberFormat.format((float) fileSizeInKb / (1024 * 1024)));
        }
    }
}

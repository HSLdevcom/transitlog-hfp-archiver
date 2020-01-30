package fi.hsl.common.filename;

public class TypedFilenameGenerator extends FileNameGenerator {


    public TypedFilenameGenerator(String csvFileName) {
        super(csvFileName);
    }

    public String generateCSVFileName(Class<?> fileType) {
        String fileName = super.generateCSVFileName();
        //Add class name to fileName
        int firstDashIndex = fileName.indexOf("-");
        StringBuilder builder = new StringBuilder(fileName);
        builder.insert(firstDashIndex, "-" + fileType.getCanonicalName() + "-");
        return builder.toString();
    }
}

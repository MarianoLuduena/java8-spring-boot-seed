package ar.com.itau.seed.helper;

public class JsonHelper {

    public static String withAnotherValue(final String json, final String attributeName, final String newValue) {
        final String newKeyValue = "\"" + attributeName + "\": \"" + newValue + "\"";
        return json.replaceAll(stringAttributeRegex(attributeName), newKeyValue);
    }

    public static String withAnotherValue(final String json, final String attributeName, final Number newValue) {
        final String newKeyValue = "\"" + attributeName + "\": " + newValue;
        return json.replaceAll(numberAttributeRegex(attributeName), newKeyValue);
    }

    public static String withNullStringAttribute(final String json, final String attributeName) {
        final String newKeyValue = "\"" + attributeName + "\": null";
        return json.replaceAll(stringAttributeRegex(attributeName), newKeyValue);
    }

    public static String withNullNumberAttribute(final String json, final String attributeName) {
        final String newKeyValue = "\"" + attributeName + "\": null";
        return json.replaceAll(numberAttributeRegex(attributeName), newKeyValue);
    }

    public static String bodyErrorCode(final String code, final String description) {
        return "{ \"code\": \"" + code + "\", \"description\": \"" + description + "\" }";
    }

    private static String stringAttributeRegex(final String attributeName) {
        return "\"" + attributeName + "\":\\s*\"[^\"]+?\"";
    }

    private static String numberAttributeRegex(final String attributeName) {
        return "\"" + attributeName + "\":\\s*\\d+(\\.)?\\d*";
    }

}

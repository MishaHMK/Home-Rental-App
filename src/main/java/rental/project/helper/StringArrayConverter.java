package rental.project.helper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StringArrayConverter implements AttributeConverter<String[], String> {

    @Override
    public String convertToDatabaseColumn(String[] attribute) {
        return attribute == null
                ? null
                : String.join(", ", attribute);
    }

    @Override
    public String[] convertToEntityAttribute(String dbData) {
        return dbData == null || dbData.isEmpty()
                ? new String[0]
                : dbData.split(", ");
    }
}

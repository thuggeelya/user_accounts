package ru.thuggeelya.useraccounts.model.search;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "users")
@EqualsAndHashCode(callSuper = true)
public class ElasticSearchUser extends SearchUser {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Date, format = DateFormat.date, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Field(type = FieldType.Double)
    private String balance;

    @Field(type = FieldType.Keyword, name = "email_data")
    private String[] emailData = new String[0];

    @Field(type = FieldType.Keyword, name = "phone_data")
    private String[] phoneData = new String[0];
}

package com.example.smd_a3;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DatabaseTable(tableName = "contacts")
public class ContactDB {

    @Setter
    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField
    private String email;

    @DatabaseField
    private String name;

    @DatabaseField
    private String contact;

    @DatabaseField
    private String img_path;

    public ContactDB() {
    }

    public String getEmail() {
        return email;
    }
    public String getImage() {
        return img_path;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
    public void setImage(String img_path) {
        this.img_path = img_path;
    }
}

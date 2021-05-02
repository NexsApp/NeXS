package com.example.nexs.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class LikedArticle {

    @PrimaryKey
    @NonNull
    private String id;

    public LikedArticle(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id.equals(((LikedArticle) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

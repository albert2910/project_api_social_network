package com.example.demospringsecurity.repository;

import com.example.demospringsecurity.model.Image;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends CrudRepository<Image, Integer> {
    List<Image> findImageByImagePostId(int postId);

    List<Image> findImageByImagePostIdAndImageFlagDelete(int postId, int imageFlagDelete);

}

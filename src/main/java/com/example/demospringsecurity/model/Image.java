package com.example.demospringsecurity.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBPost_image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_image_id")
    private int imageId;

    @Column(name = "post_image_url")
    private String imageUrl;

    @Column(name = "post_image_post_id")
    private int imagePostId;

//    imageFlagDelete = 0 => exist
//    imageFlagDelete = 1 => delete
    @Column(name = "image_flag_delete")
    private int imageFlagDelete;

}

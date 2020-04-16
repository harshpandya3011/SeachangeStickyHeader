package com.seachange.healthandsafty.presenter;

import com.seachange.healthandsafty.model.GalleryImage;
import com.seachange.healthandsafty.view.GalleryView;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class GalleryPresenterTest {

    @Test
    public void ImageListTest() {


        GalleryPresenter presenter = new GalleryPresenter(new GalleryView() {
            @Override
            public void imageArrayUpdated(@NotNull ArrayList<GalleryImage> result) {

            }

            @Override
            public void imagesCounter(int result) {
                assertEquals(0, result);
            }
        });

        ArrayList<GalleryImage> imageList = new ArrayList<>();
        presenter.countImageSelected(imageList);
    }

    @Test
    public void ImageListTapTest() {


        GalleryPresenter presenter = new GalleryPresenter(new GalleryView() {
            @Override
            public void imageArrayUpdated(@NotNull ArrayList<GalleryImage> result) {

            }

            @Override
            public void imagesCounter(int result) {

            }
        });

        ArrayList<GalleryImage> imageList = new ArrayList<>();
        GalleryImage tmp = new GalleryImage();
        tmp.setSelected(true);
        imageList.add(tmp);

        int counter = presenter.getImageSelected(imageList);
        assertEquals(1, counter);
    }
}

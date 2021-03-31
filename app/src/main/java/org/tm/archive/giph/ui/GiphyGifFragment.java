package org.tm.archive.giph.ui;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.loader.content.Loader;

import org.tm.archive.giph.model.GiphyImage;
import org.tm.archive.giph.net.GiphyGifLoader;

import java.util.List;

public class GiphyGifFragment extends GiphyFragment {

  @Override
  public @NonNull Loader<List<GiphyImage>> onCreateLoader(int id, Bundle args) {
    return new GiphyGifLoader(getActivity(), searchString);
  }

}

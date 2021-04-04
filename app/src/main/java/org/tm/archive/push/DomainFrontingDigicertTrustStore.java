package org.tm.archive.push;

import android.content.Context;

import org.tm.archive.R;
import org.whispersystems.signalservice.api.push.TrustStore;

import java.io.InputStream;

public class DomainFrontingDigicertTrustStore implements TrustStore {

  private final Context context;

  public DomainFrontingDigicertTrustStore(Context context) {
    this.context = context.getApplicationContext();
  }

  @Override
  public InputStream getKeyStoreInputStream() {
    return context.getResources().openRawResource(R.raw.censorship_digicert);
  }

  @Override
  public String getKeyStorePassword() {
    return "whisper";
  }

}

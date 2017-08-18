package com.tagtraum.perf.gcviewer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.tagtraum.perf.gcviewer.imp.DataReaderException;
import com.tagtraum.perf.gcviewer.imp.DataReaderFacade;
import com.tagtraum.perf.gcviewer.model.GCModel;
import com.tagtraum.perf.gcviewer.model.GCResource;
import com.tagtraum.perf.gcviewer.model.GcResourceFile;
import com.tagtraum.perf.gcviewer.model.GcResourceSeries;
import com.tagtraum.perf.gcviewer.view.SimpleChartRenderer;
import com.tagtraum.perf.gcviewer.view.model.GCPreferences;

public class DossierGCViewer {

  public byte[] exportImage(GCPreferences gcPreferences,
                            String gcFile) throws DataReaderException, IOException {
    GCResource gcResource = getGcResource(gcFile);

    DataReaderFacade dataReaderFacade = new DataReaderFacade();
    GCModel model = dataReaderFacade.loadModel(gcResource);

    return renderChartToByteArray(gcPreferences, model);
  }

  private byte[] renderChartToByteArray(GCPreferences gcPreferences, GCModel model) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    SimpleChartRenderer renderer = new SimpleChartRenderer();
    renderer.render(gcPreferences, model, baos);
    return baos.toByteArray();
  }

  private GCResource getGcResource(String gcFile) {
    List<String> files = Arrays.asList(gcFile.split(";"));
    List<GCResource> resources = files.stream().map(GcResourceFile::new).collect(Collectors.toList());
    if (resources.isEmpty())
      throw new IllegalStateException("Found no valid resource!");

    if (resources.size() == 1) {
      return resources.get(0);
    }
    else {
      return new GcResourceSeries(resources);
    }
  }
}

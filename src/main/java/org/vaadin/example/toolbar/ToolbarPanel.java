package org.vaadin.example.toolbar;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.vaadin.example.Miniature;
import org.vaadin.example.service.MiniaturesService;

public class ToolbarPanel extends VerticalLayout {
    private final MiniaturesService miniaturesService;

    private final ListBox<Miniature> filesListBox = new ListBox<>();
    private final BaseDetails baseDetailsPanel;
    private final PreviewDetails previewPanel;
    private final PaddingDetails paddingImagePanel;

    public ToolbarPanel(MiniaturesService miniaturesService, PreviewDetails previewPanel) {
        this.miniaturesService = miniaturesService;
        this.previewPanel = previewPanel;
        this.paddingImagePanel = new PaddingDetails(miniaturesService::selected, previewPanel::update);
        this.baseDetailsPanel = new BaseDetails(miniaturesService);
        Details uploadImagePanel = new Details("Library", getUploadImagePanel());
        uploadImagePanel.addThemeVariants(DetailsVariant.SMALL, DetailsVariant.FILLED);
        uploadImagePanel.setWidthFull();
        uploadImagePanel.setOpened(true);
        setSpacing(false);
        add(uploadImagePanel, baseDetailsPanel, paddingImagePanel);
    }

    private Component getUploadImagePanel() {
        filesListBox.setRenderer(new ComponentRenderer<Component, Miniature>(miniature -> new Span(miniature.getFileName())));
        filesListBox.setItems(miniaturesService.miniatures());
        filesListBox.setWidthFull();
        Upload upload = getUpload();
        upload.setAcceptedFileTypes(".png", ".jpg");

        filesListBox.addValueChangeListener(e -> {
            miniaturesService.select(e.getValue()).ifPresentOrElse(miniature -> {
                baseDetailsPanel.init(miniature);
                paddingImagePanel.init(miniature);
                previewPanel.init(miniature);
            }, () -> {
                baseDetailsPanel.disable();
                paddingImagePanel.disable();
                previewPanel.disable();
            });
        });

        return new VerticalLayout(filesListBox, upload);
    }

    private Upload getUpload() {
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setDropAllowed(false);

        upload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            filesListBox.setItems(miniaturesService.addAndSelect(new Miniature(fileName, buffer.getInputStream(fileName))));
            upload.clearFileList();
        });

        upload.addFileRemovedListener(event -> {
            String fileName = event.getFileName();
            filesListBox.setItems(miniaturesService.remove(fileName));
        });
        return upload;
    }
}

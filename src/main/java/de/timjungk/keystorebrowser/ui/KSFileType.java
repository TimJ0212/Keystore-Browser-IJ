package de.timjungk.keystorebrowser.ui;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class KSFileType implements FileType {

    @Override
    public @NonNls @NotNull String getName() {
        return "keystore";
    }

    @Override
    public @NlsContexts.Label @NotNull String getDescription() {
        return "A java keystore file";
    }

    @Override
    public @NlsSafe @NotNull String getDefaultExtension() {
        return "ks";
    }


    @Override
    public Icon getIcon() {
        return IconLoader.getIcon("/lock_icon.svg", KSFileType.class);
    }

    @Override
    public boolean isBinary() {
        return true;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }
}

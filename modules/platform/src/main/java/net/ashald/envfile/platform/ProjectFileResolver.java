package net.ashald.envfile.platform;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.io.File;
import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectFileResolver {
    public static final ProjectFileResolver DEFAULT = new ProjectFileResolver();

    public Optional<File> resolvePath(@NonNull final Project project, final String path) {
        if (path == null) {
            return Optional.empty();
        }

        String resolvedPath = path;
        if (!FileUtil.isAbsolute(resolvedPath)) {
            VirtualFile virtualFile;
            try {
                virtualFile = project.getBaseDir().findFileByRelativePath(resolvedPath);
            } catch (AssertionError ignored) { // can be thrown deep from IoC implementation
                virtualFile = null;
            }
            if (virtualFile != null) {
                resolvedPath = virtualFile.getPath();
            }
        }
        return Optional.of(
                new File(resolvedPath)
        );
    }
}


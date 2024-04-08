package me.supcheg.advancedmanhunt.paper.inject;

import io.papermc.paper.util.ObfHelper;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.util.proxy.DefineClassHelper;
import lombok.SneakyThrows;
import net.minecraft.world.level.chunk.storage.RegionFile;
import org.jetbrains.annotations.NotNull;

public class RegionFileStorageInject {

    @SneakyThrows
    public void inject() {
        String overwriteClass = "net.minecraft.world.level.chunk.storage.RegionFileStorageReadOverwrite";
        String targetClass = ObfHelper.INSTANCE.reobfClassName("net.minecraft.world.level.chunk.storage.RegionFileStorage");
        String targetMethod = "read";

        ClassPool classPool = ClassPool.getDefault();

        classPool.appendClassPath(new LoaderClassPath(ClassLoader.getSystemClassLoader()));
        classPool.appendClassPath(new LoaderClassPath(getClass().getClassLoader()));

        CtClass ctOverwriteClass = classPool.get(overwriteClass);
        CtClass ctTargetClass = classPool.get(targetClass);

        ctTargetClass.getDeclaredMethod(targetMethod)
                .setBody("{ return %s.%s(this, $1, $2); }".formatted(overwriteClass, targetMethod));

        loadInMainClassLoader(overwriteClass, ctOverwriteClass.toBytecode());
        loadInMainClassLoader(targetClass, ctTargetClass.toBytecode());
    }

    private void loadInMainClassLoader(@NotNull String className, byte @NotNull [] bytecode) throws CannotCompileException {
        DefineClassHelper.toClass(className, RegionFile.class, RegionFile.class.getClassLoader(), null, bytecode);
    }
}

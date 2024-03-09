package me.supcheg.advancedmanhunt.template;

import com.google.gson.Gson;
import me.supcheg.advancedmanhunt.storage.EntityRepository;
import org.jetbrains.annotations.NotNull;

public interface TemplateRepository extends EntityRepository<Template, String> {
    @NotNull
    Gson getGson();
}

package me.supcheg.advancedmanhunt.template;

import dagger.Binds;
import dagger.Module;
import me.supcheg.advancedmanhunt.template.impl.DefaultTemplateRepository;
import me.supcheg.advancedmanhunt.template.impl.ReplacingTemplateLoader;

import javax.inject.Singleton;

@Module
public interface TemplateModule {
    @Binds
    @Singleton
    Object templateService(TemplateService service);

    @Binds
    @Singleton
    TemplateRepository templateRepository(DefaultTemplateRepository repo);

    @Binds
    @Singleton
    TemplateLoader templateLoader(ReplacingTemplateLoader loader);
}

package ar.com.itau.seed.archunit;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;

@AnalyzeClasses(packages = "ar.com.itau.seed", importOptions = ImportOption.DoNotIncludeTests.class)
@SuppressWarnings("squid:S2187")
class LayeredArchitectureTest {

    private static final String DOMAIN = "Domain";
    private static final String ADAPTERS = "Adapters";
    private static final String APPLICATION = "Application";
    private static final String CONFIG = "Config";
    private static final String BASE_PKG = "ar.com.itau.seed.";

    @ArchTest
    public static final ArchRule layer_dependencies_are_respected = Architectures.layeredArchitecture()
            .layer(CONFIG).definedBy(BASE_PKG + "config..")
            .layer(DOMAIN).definedBy(BASE_PKG + "domain..")
            .layer(ADAPTERS).definedBy(BASE_PKG + "adapter..")
            .layer(APPLICATION).definedBy(BASE_PKG + "application..")

            .whereLayer(APPLICATION).mayOnlyBeAccessedByLayers(ADAPTERS, CONFIG)
            .whereLayer(ADAPTERS).mayOnlyBeAccessedByLayers(CONFIG)
            .whereLayer(DOMAIN).mayOnlyBeAccessedByLayers(APPLICATION, ADAPTERS, CONFIG);

}

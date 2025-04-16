package config;

import lombok.Getter;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

@Getter
public class PropertiesConfig {

    private final AppConfig config;

    public PropertiesConfig() {
        this.config = load();
    }

    private AppConfig load() {
        LoaderOptions options = new LoaderOptions();
        Yaml yaml = new Yaml(new Constructor(AppConfig.class, options));
        try (InputStream input = PropertiesConfig.class.getResourceAsStream("/application.yaml")) {
            return yaml.load(input);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load config", e);
        }
    }
}
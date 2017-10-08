package main.settings;

import com.colonolnutty.module.shareddata.models.settings.BaseSettings;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 2:31 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PandCSettings extends BaseSettings {
    public String[] locationsOfCrops;
    public String createPath;
    public Boolean makePerennial;
    public Boolean makeCompact;
    public Boolean makePatchFiles;
}

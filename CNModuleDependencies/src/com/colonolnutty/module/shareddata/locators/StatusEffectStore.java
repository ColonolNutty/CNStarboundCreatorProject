package com.colonolnutty.module.shareddata.locators;

import com.colonolnutty.module.shareddata.*;
import com.colonolnutty.module.shareddata.models.Ingredient;
import com.colonolnutty.module.shareddata.models.StatusEffect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 09/25/2017
 * Time: 10:02 AM
 */
public class StatusEffectStore implements IReadFiles {
    private CNLog _log;
    private FileLocator _fileLocator;
    private JsonManipulator _manipulator;
    private PatchLocator _patchLocator;
    private ArrayList<String> _fileLocations;
    private Hashtable<String, StatusEffect> _statusEffects;
    private IFileReader _fileReader;

    public StatusEffectStore(CNLog log,
                             FileLocator fileLocator,
                             JsonManipulator manipulator,
                             PatchLocator patchLocator,
                             ArrayList<String> fileLocations) {
        _log = log;
        _fileLocator = fileLocator;
        _manipulator = manipulator;
        _patchLocator = patchLocator;
        _fileLocations = fileLocations;
        _statusEffects = new Hashtable<String, StatusEffect>();
        _fileReader = new FileReaderWrapper();
        storeStatusEffects();
    }

    public StatusEffect getStatusEffect(String name) {
        storeStatusEffects();
        if(_statusEffects.containsKey(name)) {
            return _statusEffects.get(name);
        }
        return null;
    }

    public int getDefaultStatusEffectDuration(String effectName) {
        StatusEffect statusEffect = getStatusEffect(effectName);
        if(statusEffect == null || statusEffect.defaultDuration == 0) {
            return Ingredient.DefaultEffectDuration;
        }
        return statusEffect.defaultDuration;
    }

    private void storeStatusEffects() {
        if(!_statusEffects.isEmpty()) {
            return;
        }

        ArrayList<String> filePathPatches = _fileLocator.getFilePathsByExtension(_fileLocations, ".statuseffect.patch");
        ArrayList<String> filePaths = _fileLocator.getFilePathsByExtension(_fileLocations, ".statuseffect");
        for(String filePath : filePaths) {
            String patchFile = _patchLocator.locatePatchFileFor(filePath, filePathPatches);
            addStatusEffect(filePath, patchFile);
        }
    }

    private void addStatusEffect(String filePath, String patchFilePath) {
        try {
            StatusEffect statusEffect = _fileReader.read(filePath, StatusEffect.class);
            statusEffect.filePath = filePath;
            statusEffect.patchFilePath = patchFilePath;
            if(_statusEffects.containsKey(statusEffect.name)) {
                return;
            }
            if (patchFilePath != null) {
                StatusEffect patchedStatusEffect = _manipulator.applyPatch(statusEffect, patchFilePath, StatusEffect.class);
                patchedStatusEffect.filePath = filePath;
                patchedStatusEffect.patchFilePath = patchFilePath;
                _statusEffects.put(patchedStatusEffect.name, patchedStatusEffect);
            }
            else {
                _statusEffects.put(statusEffect.name, statusEffect);
            }
        }
        catch(IOException e) {
            _log.error("[IOE] While reading: " + filePath, e);
        }
    }

    @Override
    public void setFileReader(IFileReader fileReader) {
        _fileReader = fileReader;
    }
}

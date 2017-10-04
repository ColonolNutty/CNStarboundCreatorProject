package com.company.locators;

import com.company.CNLog;
import com.company.JsonManipulator;
import com.company.models.StatusEffect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 09/25/2017
 * Time: 10:02 AM
 */
public class StatusEffectStore {
    private CNLog _log;
    private FileLocator _fileLocator;
    private JsonManipulator _manipulator;
    private PatchLocator _patchLocator;
    private Hashtable<String, StatusEffect> _statusEffects;

    public StatusEffectStore(CNLog log,
                             FileLocator fileLocator,
                             JsonManipulator manipulator,
                             PatchLocator patchLocator) {
        _log = log;
        _fileLocator = fileLocator;
        _manipulator = manipulator;
        _patchLocator = patchLocator;
        _statusEffects = new Hashtable<String, StatusEffect>();
        storeStatusEffects();
    }

    public StatusEffect getStatusEffect(String name) {
        storeStatusEffects();
        if(_statusEffects.containsKey(name)) {
            return _statusEffects.get(name);
        }
        return null;
    }

    private void storeStatusEffects() {
        if(!_statusEffects.isEmpty()) {
            return;
        }

        ArrayList<String> filePathPatches = _fileLocator.getFilePathsByExtension(".statuseffect.patch");
        ArrayList<String> filePaths = _fileLocator.getFilePathsByExtension(".statuseffect");
        for(String filePath : filePaths) {
            String patchFile = _patchLocator.locatePatchFileFor(filePath, filePathPatches);
            addStatusEffect(filePath, patchFile);
        }
    }

    private void addStatusEffect(String filePath, String patchFilePath) {
        try {
            StatusEffect statusEffect = _manipulator.read(filePath, StatusEffect.class);
            statusEffect.filePath = filePath;
            statusEffect.patchFilePath = patchFilePath;
            if(_statusEffects.containsKey(statusEffect.name)) {
                return;
            }
            if (patchFilePath != null) {
                StatusEffect patchedStatusEffect = _manipulator.patch(statusEffect, patchFilePath, StatusEffect.class);
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
}

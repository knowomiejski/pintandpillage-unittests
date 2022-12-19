package nl.duckstudios.pintandpillage.testHelpers;

import nl.duckstudios.pintandpillage.entity.researching.Research;
import nl.duckstudios.pintandpillage.model.ResearchType;

import java.util.ArrayList;
import java.util.List;

public class ResearchHelper {

    public List<Research> generateResearchList() {
        List<Research> researchList = new ArrayList<>();

        Research spearResearch = new Research();
        spearResearch.setResearchLevel(0);
        spearResearch.setResearchName(ResearchType.Spear);
        researchList.add(spearResearch);

        Research shieldResearch = new Research();
        shieldResearch.setResearchLevel(1);
        shieldResearch.setResearchName(ResearchType.Shield);
        researchList.add(shieldResearch);

        Research axeResearch = new Research();
        axeResearch.setResearchLevel(1);
        axeResearch.setResearchName(ResearchType.Axe);
        researchList.add(axeResearch);

        return researchList;
    }
}

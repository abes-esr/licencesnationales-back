package fr.abes.licencesnationales.web.dto.etablissement;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@Setter
public class StatsDto {
    List<Map<String, Integer>> stats = new ArrayList<>();

    public void ajouterStat(String cle, int valeur) {
        Map<String, Integer> m = new HashMap<>();
        m.put(cle, valeur);
        this.stats.add(m);
    }
}
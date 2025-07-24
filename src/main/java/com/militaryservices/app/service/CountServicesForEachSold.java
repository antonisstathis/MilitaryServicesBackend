package com.militaryservices.app.service;

import com.militaryservices.app.dao.SoldierAccessImpl;
import com.militaryservices.app.dto.*;
import com.militaryservices.app.entity.Soldier;
import com.militaryservices.app.enums.Active;
import com.militaryservices.app.enums.Situation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CountServicesForEachSold {

    @Autowired
    SoldierAccessImpl soldierAccess;

    public List<SoldierProportion> getProportions(Set<Soldier> armedSoldiers,Set<Soldier> unarmedSoldiers,List<Soldier> allSoldiers,
                                                  Map<Integer,Soldier> soldierMap,boolean mode,String armed,boolean isPersonnel) {

        CountServices datas = getHistoricalData(armedSoldiers,unarmedSoldiers,allSoldiers,soldierMap,isPersonnel);
        List<SoldierProportion> proportions = new ArrayList<>();

        int countServices = 0;
        int countOut = 0;
        float proportion = 0;
        for(Soldier soldier : allSoldiers) {
            if(mode || soldier.getSituation().equals(armed)) {
                countServices = countAllServices(countServices, soldier, datas);
                countOut = addServices(datas.getHdForOut().get(soldier.getId()), countOut);
                proportion = (countServices != 0 && countOut != 0) ? ((float) countServices) / ((float) countOut) : proportion;
                proportion = (countServices == 0 && countOut != 0) ? 0 : proportion;
                proportion = (countServices != 0 && countOut == 0) ? Float.MAX_VALUE : proportion;
                proportions.add(new SoldierProportion(soldier.getId(), proportion));
                countServices = 0;
                countOut = 0;
            }
        }

        return proportions;
    }

    private CountServices getHistoricalData(Set<Soldier> armedSoldiers,Set<Soldier> unarmedSoldiers,List<Soldier> allSoldiers, Map<Integer,Soldier> soldierMap,boolean isPersonnel) {
        List<HistoricalData> armedServices = soldierAccess.getHistoricalDataDesc(allSoldiers.get(0).getUnit(), Situation.ARMED.name().toLowerCase(),isPersonnel);
        List<HistoricalData> unarmedServices = soldierAccess.getHistoricalDataDesc(allSoldiers.get(0).getUnit(),Situation.UNARMED.name().toLowerCase(),isPersonnel);
        List<HistoricalData> out = soldierAccess.getHistoricalDataDesc(allSoldiers.get(0).getUnit(), Active.getFreeOfDuty(),isPersonnel);
        if(out.size()<allSoldiers.size())
            addTheRestOnes(out,soldierMap);
        if(unarmedServices.size()<unarmedSoldiers.size())
            addTheRestOnes(armedServices,soldierMap);
        if(armedServices.size()<armedSoldiers.size())
            addTheRestOnes(unarmedServices,soldierMap);
        Map<Integer,HistoricalData> armedServicesMap = createMap(armedServices);
        Map<Integer,HistoricalData> unarmedServicesMap = createMap(unarmedServices);
        Map<Integer,HistoricalData> outMap = createMap(out);
        CountServices countServices = new CountServices(armedServicesMap,unarmedServicesMap,outMap);

        return countServices;
    }

    private int countAllServices(int countServices,Soldier soldier,CountServices datas) {
        countServices = addServices(datas.getHdForArmed().get(soldier.getId()),countServices);
        countServices = addServices(datas.getHdForUnarmed().get(soldier.getId()),countServices);

        return countServices;
    }

    private int addServices(HistoricalData historicalData,int countServices) {
        return historicalData!=null ? ((int) historicalData.getNumberOfServices()) + countServices : countServices;
    }

    public void addTheRestOnes(List<HistoricalData> historicalData, Map<Integer,Soldier> soldierMap) {
        Map<Integer,Soldier> allWithHistoricalData = new HashMap<>();
        Soldier sold;
        for(HistoricalData hd : historicalData){
            sold = soldierMap.get(hd.getSoldierId());
            allWithHistoricalData.put(sold.getId(),sold);
        }

        Integer soldId;
        HistoricalData hd;
        for (Map.Entry<Integer, Soldier> entry : soldierMap.entrySet()) {
            soldId = entry.getKey();
            if(!allWithHistoricalData.containsKey(soldId)){
                hd = new HistoricalData(soldId,0);
                historicalData.add(hd);
            }
        }
    }

    private Map<Integer,HistoricalData> createMap(List<HistoricalData> historicalDatas) {

        Map<Integer,HistoricalData> map = new HashMap<>();
        for(HistoricalData historicalData : historicalDatas)
            map.put(historicalData.getSoldierId(), historicalData);

        return map;
    }

}

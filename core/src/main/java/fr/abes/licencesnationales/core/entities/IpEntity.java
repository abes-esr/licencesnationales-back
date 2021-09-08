package fr.abes.licencesnationales.core.entities;

import fr.abes.licencesnationales.core.constant.Constant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "Ip")
@NoArgsConstructor
@Getter @Setter
public class IpEntity implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "ip_Sequence")
    @SequenceGenerator(name = "ip_Sequence", sequenceName = "IP_SEQ", allocationSize = 1)
    private Long id;

    @NotBlank(message="L'IP est obligatoire")
    @Pattern(regexp = "^((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])$", message = "L'IP fournie n'est pas valide")
    private String ip;

    @NotBlank
    private boolean validee;

    private Date dateCreation;

    private Date dateModification;

    @NotBlank(message="Le type d'acces est obligatoire")
    private String typeAcces;

    @NotBlank(message="Le type d'IP est obligatoire")
    private String typeIp;

    private String commentaires;

    @ManyToOne
    private EtablissementEntity etablissement;


    public IpEntity(Long id, String ip, String typeAcces, String typeIp, String commentaires) {
        this.id = id;
        this.ip = ip;
        this.validee = false;
        this.dateCreation = new Date();
        this.typeAcces = typeAcces;
        this.typeIp=typeIp;
        this.commentaires=commentaires;


    }



    // Récupère la première ou dernière adresse d'une plage IP
    // paramètre:
    // 1: renvoi la première
    // 2: renvoi la dernière
    private String getIP(String whichValue){
        String temp = "";
        String separator = (ip.contains("."))?".":":";
        String splitter = (separator.equals("."))?"\\.":":";

        String[] values = ip.trim().split(splitter);
        int base = (typeIp.equals("ipv4"))?10:16;
        int i=0;
        for(String value:values){
            value = value.trim().replaceAll("(.*)-(.*)","$"+whichValue);
            temp = temp +Integer.parseInt(value,base);
            i++;
            if (i!=values.length)
                temp += separator;
        }
        return temp;
    }

    public String getLowestIP(){
        return getIP("1");
    }

    public String getHighestIP(){
        return getIP("2");
    }



    // Renvoi l'adresse IP sous forme de chaine de caractère.
    // Rajoute un padding à gauche des nombres
    //
    // Ex:
    // 192.168.1.2
    // retournera
    // 192168001002
    //
    // Dans le cas d'une plage d'adresse, on récupère la plus petite adresse
    // de la plage ou la plus grande en fonction du paramètre 'whichValue' ( 1 ou 2 )
    private String getValue(String whichValue){
        String temp = "";
        String[] values = ip.trim().split("\\.|:");
        int base = (typeIp.equals("ipv4"))?10:16;
        int length = (typeIp.equals("ipv4"))?3:4;
        for(String value:values){
            value = value.trim().replaceAll("(.*)-(.*)","$"+whichValue);
            temp = temp + String.format("%0" + length + "d",Integer.parseInt(value,base));
        }
        return temp;
    }

    public String getLowestValue(){
        return getValue("1");
    }

    public String getHighestValue(){
        return getValue("2");
    }



    private String[] getRange(Integer whichRange){
        String[] range = {"0","1"};
        String[] values = ip.split("\\.|:");
        int i = 0;
        for(String value:values){
            if (value.matches("(.*)-(.*)")){
                range[i] = value.split("-")[whichRange];
                i = ++ i;
            }
        }
        return range;
    }

    // Utilisé par la fusion d'accès..
    private void replaceRange(Integer whichRange,String[] ranges){
        String ip = "";
        String[] values = ip.split("\\.|:");
        int i = 0;
        for(String value:values){
            if (value.matches("(.*)-(.*)"))
                value = value.replaceAll("(.*)-(.*)",(whichRange==0)?ranges[0]+"-$2":"$1-"+ranges[1]);
            ip = ip + value;
            i = ++ i;
            if (i!=values.length)
                ip = ip + ".";
        }
        ip = ip;
    }

    public String[] getLowestRange(){
        return getRange(0);
    }

    public String[] getHighestRange(){
        return getRange(1);
    }

    // Fusionne sans vérifier que les plages se chevauchent
    // ni sans vérifier si les adresses correspondents
    //
    // NON UTILISE JUSQU'A PRESENT
    public void fusionnerAcces(IpEntity a){
        // Si les ip sont de même type (ipv4 ou ipv6)
        if (this.getTypeIp().equals(a.getTypeIp())){
            if (this.getTypeAcces().equals("ip") && a.getTypeAcces().equals("plage")){
                this.ip = a.getIp();
                this.setTypeAcces("range");
            }else{
                // Si la plus petite ip de la plage est supérieure à celle que l'on veut fusionner
                if (this.getLowestValue().compareTo(a.getLowestValue())>0)
                    // On remplace la première ip de la plage par celle de l'adresse avec laquelle on fusionne
                    this.replaceRange(0,a.getLowestRange());
                // Si la plus haute  ip de la plage est inférieure à celle que l'on veut fusionner
                if (this.getHighestValue().compareTo(a.getHighestValue())<0)
                    // On remplace la seconde ip de la plage par celle de l'adresse avec laquelle on fusionne
                    this.replaceRange(1,a.getHighestRange());
            }
        }
    }

    // Vérifie si l'ip est réservée
    public Integer isReserved(){
        if ((this.getLowestValue().compareTo("010000000000")>=0)&&
                (this.getHighestValue().compareTo("010255255255")<=0)){
            return Constant.IP_RESERVED;
        }

        if ((this.getLowestValue().compareTo("19216800000")>=0)&&
                (this.getHighestValue().compareTo("192168255255")<=0)){
            return Constant.IP_RESERVED;
        }

        if ((this.getLowestValue().compareTo("169254000000")>=0)&&
                (this.getHighestValue().compareTo("169254255255")<=0)){
            return Constant.IP_RESERVED;
        }

        if ((this.getLowestValue().compareTo("172016000000")<=0 && this.getHighestValue().compareTo("172016000000")>=0)
                ||(this.getLowestValue().compareTo("172031255255")<=0 && this.getHighestValue().compareTo("172031255255")>=0)
                ||(this.getLowestValue().compareTo("172016000000")>=0 && this.getHighestValue().compareTo("172031255255")<=0))
            return Constant.IP_RESERVED;

        return Constant.IP_NOT_CONTAINED;
    }

    // Nous informe si l'IP ou la PLAGE ...
    //      - est la même
    //      - est contenu
    //      - contient
    //      - chevauche
    // ... l'IP ou la PLAGE passée en paramètre
    public Integer contains(IpEntity acces){
        if (this.getTypeIp().equals(acces.getTypeIp())){
            if (this.getLowestValue().compareTo(acces.getLowestValue())==0
                    && this.getHighestValue().compareTo(acces.getHighestValue())==0)
                return Constant.IP_SAME;

            if (this.getLowestValue().compareTo(acces.getLowestValue())>=0
                    && this.getHighestValue().compareTo(acces.getHighestValue())<=0)
                return Constant.IP_CONTAINED;

            if (this.getLowestValue().compareTo(acces.getLowestValue())<=0
                    && this.getHighestValue().compareTo(acces.getHighestValue())>=0)
                return Constant.IP_CONTAINS;

            if (this.getLowestValue().compareTo(acces.getLowestValue())>=0
                    && this.getLowestValue().compareTo(acces.getHighestValue())<=0
                    && this.getHighestValue().compareTo(acces.getHighestValue())>=0)
                return Constant.IP_CROSS;

            if (this.getHighestValue().compareTo(acces.getLowestValue())>=0
                    && this.getLowestValue().compareTo(acces.getLowestValue())<=0
                    && this.getHighestValue().compareTo(acces.getHighestValue())>=0)
                return Constant.IP_CROSS;
        }
        return Constant.IP_NOT_CONTAINED;
    }

}

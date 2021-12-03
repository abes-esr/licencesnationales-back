package fr.abes.licencesnationales.batch.relance;

import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EventService;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Component
public class TraiterEtabSansIpTasklet implements Tasklet, StepExecutionListener {
    private List<EtablissementEntity> etabSansIp;

    private final EmailService emailService;

    private final EventService eventService;

    @Autowired
    public TraiterEtabSansIpTasklet(EmailService emailService, EventService eventService) {
        this.emailService = emailService;
        this.eventService = eventService;
    }
    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.etabSansIp = (List<EtablissementEntity>) stepExecution.getExecutionContext().get("etabSansIp");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Calendar dateSuppressionEtab = new GregorianCalendar();
        for (EtablissementEntity etab : etabSansIp) {
            //calcul de la date de suppression de l'établissement
            Date dateSuppressionDerniereIp = eventService.getLastDateSuppressionIpEtab(etab);
            if (dateSuppressionDerniereIp != null) {
                //si on a la date de dernière suppression d'une IP de l'etab, on ajoute un an pour avoir la date de suppression de l'établissement
                dateSuppressionEtab.setTime(dateSuppressionDerniereIp);
            } else {
                //on récupère la date de création de l'établissement
                Date dateCreationEtab = eventService.getDateCreationEtab(etab);
                dateSuppressionEtab.setTime(dateCreationEtab);
            }
            dateSuppressionEtab.add(Calendar.YEAR, 1);
            emailService.constructRelanceEtabMail(etab.getNom(), etab.getContact().getMail(), format.format(dateSuppressionEtab.getTime()));
        }
        return RepeatStatus.FINISHED;
    }
}

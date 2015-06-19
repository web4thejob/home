package org.web4thejob.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.web4thejob.context.ContextUtil;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import java.util.Properties;

public class TestMailSenderController extends SelectorComposer<Window> {
    @Wire
    private Textbox from;
    @Wire
    private Textbox to;
    @Wire
    private Textbox subject;
    @Wire
    private Textbox body;


    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);
    }

    @Listen("onClick=#send")
    public void send() {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        JavaMailSenderImpl mailSender = ContextUtil.getBean(JavaMailSenderImpl.class);
        mailSender.setHost("smtp-mail.outlook.com");
        mailSender.setPort(587);
        mailSender.setUsername("bissaias@hotmail.com");
        mailSender.setPassword("polika25");
        mailSender.setJavaMailProperties(props);


        SimpleMailMessage msg = new SimpleMailMessage();

        msg.setFrom(from.getValue());
        msg.setTo(to.getValue());
        msg.setSubject(subject.getValue());
        msg.setText(body.getValue());
        mailSender.send(msg);
    }
}

package org.web4thejob.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.web4thejob.context.ContextUtil;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
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

    @Wire
    private Textbox host;
    @Wire
    private Intbox port;
    @Wire
    private Textbox username;
    @Wire
    private Textbox password;
    @Wire
    private Checkbox tls;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);
    }

    @Listen("onClick=#send")
    public void send() {


        JavaMailSenderImpl mailSender = ContextUtil.getBean(JavaMailSenderImpl.class);
        mailSender.setHost(host.getValue());
        mailSender.setPort(port.getValue());
        mailSender.setUsername(username.getValue());
        mailSender.setPassword(password.getValue());

        if (tls.isChecked()) {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            mailSender.setJavaMailProperties(props);
        }

        SimpleMailMessage msg = new SimpleMailMessage();

        msg.setFrom(from.getValue());
        msg.setTo(to.getValue());
        msg.setSubject(subject.getValue());
        msg.setText(body.getValue());
        mailSender.send(msg);
    }
}

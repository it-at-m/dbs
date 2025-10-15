package de.muenchen.oss.dbs.ticketing.eventing.mailhandler.domain.mapper;

import de.muenchen.oss.dbs.ticketing.eai.client.model.ArticleAttachment;
import de.muenchen.oss.dbs.ticketing.eventing.mailhandler.domain.model.MailMessage;
import java.io.InputStream;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface ZammadMapper {
    MailMessage.Attachment toMailAttachment(ArticleAttachment attachment, InputStream content);
}

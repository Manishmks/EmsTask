package emstask.spring.util;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Service;

import java.util.Locale;
@Service
public class MessageUtil implements MessageSourceAware
{
    private MessageSource source;

    // getting message with no parameter
    @Override
    public void setMessageSource(MessageSource messageSource)
    {
        this.source=messageSource;
    }
    // getting message with one parameter
    public String getMessage(String tag)
    {
        return  this.getMessage(tag,null);
    }
    // getting message with multiple parameter
    public String getMessage(String tag,Object params[])
    {
        return  source.getMessage(tag,params, Locale.US);
    }
}



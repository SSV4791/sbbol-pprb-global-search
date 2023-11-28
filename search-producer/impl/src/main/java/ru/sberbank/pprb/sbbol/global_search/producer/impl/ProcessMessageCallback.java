package ru.sberbank.pprb.sbbol.global_search.producer.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.NonNull;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.sberbank.pprb.sbbol.global_search.producer.exception.KafkaProducerException;

/**
 * Callback для обработки результатов отправки сообщения в топик global-search.
 *
 * @param <T> тип сообщения
 */
public class ProcessMessageCallback<T> implements ListenableFutureCallback<SendResult<String, T>> {

    private static final Logger logger = LoggerFactory.getLogger(ProcessMessageCallback.class);

    private final T message;

    public ProcessMessageCallback(T message) {
        this.message = message;
    }

    @Override
    public void onFailure(@NonNull Throwable ex) {
        throw new KafkaProducerException(message.toString(), ex);
    }

    @Override
    public void onSuccess(SendResult<String, T> result) {
        if (result != null) {
            logger.debug("Сообщение \"{}\" отправлено, offset = {}", message, result.getRecordMetadata().offset());
        } else {
            logger.debug("Сообщение \"{}\" отправлено, result = null", message);
        }
    }
}

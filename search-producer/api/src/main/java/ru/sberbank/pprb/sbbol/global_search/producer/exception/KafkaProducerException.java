package ru.sberbank.pprb.sbbol.global_search.producer.exception;

public class KafkaProducerException extends RuntimeException {

    private static final String ERROR_PREFIX = "Невозможно отправить сообщение: ";

    public KafkaProducerException(String message, Throwable cause) {
        super(ERROR_PREFIX.concat(message), cause);
    }
}

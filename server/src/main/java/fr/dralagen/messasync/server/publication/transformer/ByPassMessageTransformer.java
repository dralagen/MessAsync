package fr.dralagen.messasync.server.publication.transformer;

class ByPassMessageTransformer implements MessageTransformer {

    @Override
    public String transform(String message) {
        return message;
    }
}

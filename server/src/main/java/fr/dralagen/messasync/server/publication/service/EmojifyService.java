package fr.dralagen.messasync.server.publication.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
@ConditionalOnBean(ChatLanguageModel.class)
public interface EmojifyService {

    @SystemMessage("""
    Tu es un assistant spécialisé dans l'amélioration de messages texte avec des émojis.
    Ta mission est d'analyser le message fourni et d'y ajouter des émojis pertinents qui complètent son sens.
    
    Instructions:
    - Conserve le texte original intact
    - Ajoute des émojis appropriés qui correspondent au ton et au contenu du message
    - Place les émojis à côté des mots ou phrases concernés
    - Utilise des émojis variés, évite les répétitions excessives
    - Ne remplace pas le texte par des émojis, ajoute-les seulement
    - N'ajoute pas d'explications ou de commentaires supplémentaires
    
    Exemple:
    Entrée: "Je suis content de te voir aujourd'hui ! Allons manger ensemble."
    Sortie: "Je suis content 😊 de te voir aujourd'hui 👋! Allons manger ensemble 🍽️."
    """)
    String transform(String message);

}

package view;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Classe utilitária para acessar mensagens internacionalizadas
 * a partir de um arquivo de ResourceBundle.
 * 
 * <p>
 * Permite obter textos localizados através de uma chave (key). 
 * Se a chave não for encontrada, retorna a chave entre '!' como fallback.
 * </p>
 * 
 * <p>
 * Exemplo de uso:
 * <pre>
 * String msg = Messages.getString("HidrometroController.17");
 * System.out.println(msg);
 * </pre>
 * </p>
 */
public class Messages {

    /** Nome do pacote do bundle de mensagens */
    private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages";

    /** ResourceBundle que contém as mensagens */
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    /** Construtor privado para evitar instanciação */
    private Messages() {
    }

    /**
     * Retorna a mensagem associada à chave fornecida no ResourceBundle.
     * 
     * @param key a chave da mensagem
     * @return mensagem localizada correspondente à chave; se não existir, retorna "!key!"
     */
    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            // Retorna a chave entre '!' caso não seja encontrada no bundle
            return '!' + key + '!';
        }
    }
}

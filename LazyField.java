package marc;

/**
 * интерфейс для ленивой инициализации карты подполей
 * @author ktwice
 */
public interface LazyField {
/**
 * вытаскивает строковое представление поля по его метке
 * @param i - метка поля
 * @return строковое представление поля
 */
    public String getString(int i);
}

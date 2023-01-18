package edu.hdu.hziee.betastudio.util.customenum.basic;

/**
 * 该工具类只适用于实现Key-Value形式的枚举类
 * {@link KVenum}
 */
public class EnumUtil {

    /**
     * 判断是否存在code值
     */
    public static <E> boolean isExist(KVenum<E,?>[] enums,E code){
        if(code==null) return false;

        for (KVenum value : enums) {
            if(code.equals(value.getCode())){
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<? extends KVenum<V,?>>,V> boolean isExist(Class<E> enumClass,V code){
        if(code==null) return false;

        for (Enum<? extends KVenum<V,?>> e : enumClass.getEnumConstants()) {
            if(code.equals(((KVenum<V,?>) e).getCode())){
                return true;
            }
        }
        return false;
    }

    /**
     * 通过code获取枚举类
     *
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<? extends KVenum<V,?>>,V> E getEnumByCode(E[] enums,V code){
        if(code==null){
            return null;
        }
        for (E e : enums) {
            if(code.equals(((KVenum<V,?>) e).getCode())){
                return e;
            }
        }
        return null;
    }

    public static <E extends Enum<? extends KVenum<V,?>>,V> E getEnumByCode(Class<E> enumClass,V code){
        return getEnumByCode(enumClass.getEnumConstants(),code);
    }

    /**
     * 根据描述返回enum
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<? extends KVenum<?,T>>,T> E getEnumByDesc(E[] enums,T desc){
        if(desc==null) return null;

        for (E e : enums) {
            if(desc.equals(((KVenum<?,T>) e).getDesc())){
                return e;
            }
        }
        return null;
    }

    public static <E extends Enum<? extends KVenum<?,T>>,T> E getEnumByDesc(Class<E> enumClass,T desc){
        return getEnumByDesc(enumClass.getEnumConstants(),desc);
    }
}

package gp.pyinsa.jroms.utils;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class CustomBeanUtils {

    /*
     * Get null properties as a Set<String> from an Object
     */
    public static Set<String> getNullPropertyNamesAsSet(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (PropertyDescriptor pd : pds) {
            String propertyName = pd.getName();

            if (src.getPropertyValue(propertyName) == null || src.getPropertyValue(propertyName) == "")
                emptyNames.add(propertyName);
        }

        return emptyNames;
    }

    /*
     * Get null properties as a String Array from an object
     */
    public static String[] getNullPropertyNames(Object source) {
        Set<String> emptyNames = getNullPropertyNamesAsSet(source);

        return emptyNames.toArray(new String[emptyNames.size()]);
    }

    /*
     * Get null properties as a String Array from an object. You can add custom
     * properties to be included even when they're not null
     */
    public static String[] getNullPropertyNames(Object source, String... ignoreProperties) {
        Set<String> emptyNames = getNullPropertyNamesAsSet(source);

        for (String property : ignoreProperties) {
            emptyNames.add(property);
        }

        return emptyNames.toArray(new String[emptyNames.size()]);
    }

    public static void copyNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));

    }

    public static void copyNonNullProperties(Object source, Object target, String... ignoreProperties) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source, ignoreProperties));
    }

}
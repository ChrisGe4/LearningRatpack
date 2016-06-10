package app.GORM;

import org.grails.datastore.gorm.GormEntity;

/**
 * @author Chris.Ge
 */
class Person implements GormEntity<Person> {
    Long id
    Long version
    String name
    static constraints =

            {
                name blank: false
            }
}

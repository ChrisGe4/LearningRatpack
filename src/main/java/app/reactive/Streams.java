package app.reactive;

/**
 * @author Chris.Ge
 */
public class Streams {

    //use route() to filter, if check null can use notNull instead.
    //When working with two Promise type calls, where the second call is not dependent upon data
    //* from the first, we can simply use the composition process by building a Pair type object instead
    //* if flatMap  ==>  /*method return promise*/(promise).right().map(pair->{pair.left... pair.right})

    //publisher is like the stream, call bindExec is a good practice

    //RXJava use observable /subscribe()


    //https://github.com/ReactiveX/RxJava/wiki/Backpressure how to handle over-producing Observables (buffer + debounce  or window)

}

Usage
----

Input boards like this:

```clojure
(def board 
  [[:s :s :s :s :s]
   [:s :s :b :b :s]
   [:s :b :w :w :b]
   [:s :b :w :w :b]
   [:s :s :b :b :s]])
```

To test if a piece at a given index is alive, write e.g.

```clojure
(alive? [3 3] board)
```

Likewise:

```clojure
(dead? [3 3] board)
```

You can count all the "paths to life" by running

```clojure
(alive-all [1 3] board)
```

To make (and nicely format) the first two 6X6 boards for which black is dead at [3 3], put:

```clojure
(format-boards (make-boards 2 [6 6] :b dead [3 3]))
```

Take a look at the code for alive or dead to see core.logic in action.

Issues
---- 

Large boards do not work.  The current limit seems to be 15X15.

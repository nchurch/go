(ns go.core
  (:require [clojure.string :as s])
  (:refer-clojure :exclude [==])
  (:use clojure.core.logic))

;example board
(def board
  [[:s :s :s :s :s]
   [:s :s :b :b :s]
   [:s :b :w :w :b]
   [:s :b :w :w :b]
   [:s :s :b :b :s]])

;utils for navigating and testing board

(defn right [[x y]] 
  [(inc x), y])

(defn left [[x y]]
  [(dec x), y])

(defn top [[x y]]
  [x, (dec y)])

(defn bottom [[x y]]
  [x, (inc y)])

(defn edge? [[x y] board]
  (if (or 
        (or (< x 0) (>= x (count (board 0))))
        (or (< y 0) (>= y (count board)))) true false))
  
(defn color [piece board]
  ((board (piece 1)) (piece 0)))

(defn opposite [pcolor]
  (cond
    (= pcolor :b) :w
    (= pcolor :w) :b))

;A piece is alive if there is a rectilinear path
;to a space from it; there is a rectilinear path
;to a space from it either if it is adjacent to 
;a space on some side, or there is a rectilinear path
;to a space from one of its neighbors.

(defn alive 
  "the core function"
  [piece board]
    (letfn [(open [pcolor piece board visited]
                  (cond
                    (edge? piece board) u#
                    (some #{piece} visited) u#
                    :else (conde 
                            [(== (color piece board) :s) s#]
                            [(== (color piece board) pcolor) (r-alive pcolor piece board visited)]
                            [(== (color piece board) (opposite pcolor)) u#])))
            (r-alive [pcolor piece board visited]
                     (let
                       [visited (conj visited piece)]
                       (conde
                         [(open pcolor (right piece) board visited)]
                         [(open pcolor (left piece) board visited)]
                         [(open pcolor (top piece) board visited)]
                         [(open pcolor (bottom piece) board visited)])))]
      (r-alive (color piece board) piece board #{})))

;a piece is dead if all possible rectilinear paths from it end "blocked": either at an edge
;or a piece of the other color.  This is true if either the piece is completely surrounded
;on all four corners by edges or the other color; or if for each of the adjacent pieces of its own color
;all possible rectilinear paths end blocked.

(defn dead 
  "the core function"
  [piece board]
    (letfn [(closed [pcolor piece board visited]
                  (cond
                    (edge? piece board) s#
                    (some #{piece} visited) s#
                    :else (conde 
                            [(== (color piece board) :s) u#]
                            [(== (color piece board) pcolor) (r-dead pcolor piece board visited)]
                            [(== (color piece board) (opposite pcolor)) s#])))
            (r-dead [pcolor piece board visited]
                     (let
                       [visited (conj visited piece)]
                       (all
                         (closed pcolor (right piece) board visited)
                         (closed pcolor (left piece) board visited)
                         (closed pcolor (top piece) board visited)
                         (closed pcolor (bottom piece) board visited))))]
      (r-dead (color piece board) piece board #{})))

;some utils for handling generated boards
                
(defn symbolize [[n1 n2]]
  (symbol (str "m" n1 "-" n2))) 

(defn desymbolize [nvar]
  (map read-string (s/split (subs (name nvar) 1) #"-")))

(defn tablefy [row-length coll]
  (loop [res []
         mcoll (vec coll)]
    (if (<= (count mcoll) 0) res
      (recur (conj res (subvec mcoll 0 row-length))
             (subvec mcoll row-length (count mcoll)))))) 

(defn mapv-in
  "maps f over a nested vector structure"
  ([f coll]
    (let [f #(if (coll? %)(mapv-in f %)(f %))]
     (-> (reduce (fn [v o] (conj! v (f o))) (transient []) coll)
         persistent!))))

;use these!

(defn alive-all 
  "tell me how many paths this piece has to stay alive"
  [piece board]
  (run* [q]
        (alive piece board)
        (== q true)))

(defn alive? 
  "simple predicate whether to keep"
  [piece board]
  (first (run 1 [q]
        (alive piece board)
        (== q true))))

(defn dead? 
  "simple predicate whether to piece is dead"
  [piece board]
  (first (run 1 [q]
        (dead piece board)
        (== q true))))

(defn test-all-pieces
  "test all the pieces and display whether they are alive or dead; default to alive" 
  ([board pred]
  (for [y (range 0 (count board))]
    (for [x (range 0 (count (board 0)))]
      (cond (= (color [x y] board) :e) ":e  "
            (= (color [x y] board) :s) ":s  "
            :else (if (pred [x y] board)
                    (str (color [x y] board) " " "t")
                    (str (color [x y] board) " " "f"))))))
  ([board](test-all-pieces board alive?)))

(defmacro make-boards 
  "generate first Num boards of dimension Dims for which piece of color Pcolor is Lfun at Index.  
   Pcolor should be either :b or :w; lfun should be either alive or dead, NOT the predicates"
  [num dims pcolor lfun index]
  (let [positions
        (for [x (range 0 (dims 0))
              y (range 0 (dims 1))]
          (symbolize [x y]))
        board (tablefy (dims 0) 
                       positions)]
    `(map #(tablefy ~(dims 0) %) 
          (run ~num [q#]
                (fresh [~@positions]
                       (== ~(symbolize index) ~pcolor)
                       (~lfun ~index ~board)
                       (== q# [~@positions]))))))

(defn format-boards 
  "print out all the boards so they line up nicely"
  [boards]
  (mapv-in #(cond
              (nil? %) :n 
              (= (first (name %)) \_) :_ 
              :else %) boards))
          

    
       



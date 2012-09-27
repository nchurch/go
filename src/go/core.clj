(ns go.core
  (:refer-clojure :exclude [==])
  (:use clojure.core.logic))

;example board
(def board
  [[:e :e :e :e :e :e :e]
   [:e :s :s :s :s :s :e]
   [:e :s :s :b :b :s :e]
   [:e :s :b :w :w :b :e]
   [:e :s :b :w :w :b :e]
   [:e :s :s :b :b :s :e]
   [:e :e :e :e :e :e :e]])

;A piece is alive if there is a rectilinear path
;to a space from it; there is a rectilinear path
;to a space from it either if it is adjacent to 
;a space on some side, or there is a rectilinear path
;to a space from one of its neighbors.

(defn right [[x y]] 
  [(inc x), y])

(defn left [[x y]]
  [(dec x), y])

(defn top [[x y]]
  [x, (dec y)])

(defn bottom [[x y]]
  [x, (inc y)])

(defn color [piece board]
  ((board (piece 1)) (piece 0)))

(defn alive [piece board]
  (let [visited (atom #{})]
    (letfn [(open [pcolor piece board]
                  (cond
                    (some #{piece} @visited) u#
                    (= (color piece board) :s) s#
                    (= (color piece board) :e) u#
                    (not= (color piece board) pcolor) u#
                    :else (r-alive pcolor piece board)))
            (r-alive [pcolor piece board]
              (do
                (swap! visited conj piece)
                (conde
                  [(open pcolor (right piece) board)]
                  [(open pcolor (left piece) board)]
                  [(open pcolor (top piece) board)]
                  [(open pcolor (bottom piece) board)])))]
      (r-alive (color piece board) piece board))))
                
(defn keep-all [piece board]
  (run* [q]
        (alive piece board)
        (== q true)))

(defn keep? [piece board]
  (first (run 1 [q]
        (alive piece board)
        (== q true))))

(defn test-all-pieces [board]
  (for [y (range 0 (count board))]
    (for [x (range 0 (count (board 0)))]
      (cond (= (color [x y] board) :e) ":e  "
            (= (color [x y] board) :s) ":s  "
            :else (if (keep? [x y] board)
                    (str (color [x y] board) " " "t")
                    (str (color [x y] board) " " "f"))))))
    
       



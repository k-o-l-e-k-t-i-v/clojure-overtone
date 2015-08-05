(ns late-night.core
  (:use [overtone.core])
  )

;(use 'overtone.core :reload)
(defn hello-world
  ([] (println "night --> late night code is ready"))
  ([x] (println (str "night --> hello " x " the ruller of the world"))))

(defn seq-player
  [metro beat pattern ref fnc]
  (let [t1 (metro beat)
        x  (choose [0])
        t2 (metro (+ x (inc beat)))
        p  (if (not (= () (rest pattern)))
             (rest pattern)
             (let [o @ref
                   c (count o)
                   t (take c o)]
               o))
        g  (first pattern)]
    (at t1
        (if (= g 1)
          (let [r    (+ 1 (rand-int 19))
                prob 0]
            (if (> r prob)
              (fnc)
              (println "mind the gap")))
          ))
    (apply-at t2 #'seq-player metro (+ x (inc beat)) p ref fnc [])))

(defn zero-beat [metro m]
  (let [b (metro)
        r (- m (mod b m))
        zero-beat (+ b r)]
    zero-beat))








(println "night --> functions passed")
(println "night --> trying to boot supercollider server")

;(cider)











(if (not (server-connected?))
                                        ;  check if server is connected than boot
  (boot-external-server)
  (println "night --> server is already connected"))
(hello-world)

(ns late-night.synths
  (:use [overtone.core]))

(defsynth night-bd-1

  [freq-mod 1800
   freq-base 40]
  (let [e (env-gen (perc 0 0.02 1 0) 1 freq-mod freq-base 1)
        e2 (env-gen (perc 0 0.02 1 0) 1 (* 0.5 freq-mod) freq-base 1)
        a (env-gen (perc 0 0.12 1 0) 1 1 0 1)
        a2 (env-gen (perc 0 0.12 1 0) 1 1 0 1)

        s (sin-osc-fb e 0.5)
        s (distort (* 4 e s))
        s (resonz s (* 4 e) 0.5)
        sa (* s a)

        s2 (saw e2)
        s2 (distort (* 40 e2 s2))
        s2 (rlpf s2 (* 1 e2) 0.915)
        sa2 (* s2 a2)

        saa (env-gen (perc 0 0.061 1 -5) 1 1 0 1)
        sa3 (g-verb:ar sa2 23 0.883 0.05)
        sa3 (freq-shift sa3 (* saa 2000))

        o (mix [sa sa2 (* 0.523 sa3)])
        o (distort (* 20 o) )

        x (detect-silence o 0.001 0.25 FREE)]
    (out 0 (pan2 o 0 1))))


(defsynth night-hat-1
  [freq-mod 90
   freq-base 4000]
  (let [e (env-gen (perc 0 0.02 1 0) 1 freq-mod freq-base 1)
        e2 (env-gen (perc 0 0.02 1 0) 1 (* 0.5 freq-mod) freq-base 1)
        a (env-gen (perc 0 0.2 1 0) 1 1 0 1)
        a2 (env-gen (perc 0 0.2 1 0) 1 1 0 1)

        s (sin-osc-fb e 4.5)
        s (distort (* 20 e s))
        s (rhpf s (* 4 e) 0.85)
        sa (* s a)

        s2 (sin-osc-fb e2 3)
        s2 (distort (* 20 e2 s2))
        s2 (bpf s2 (* 4 e2) 0.0155)
        sa2 (* s2 a2)

        saa (env-gen (perc 0 0.21 1 -5) 1 1 0 1)
        sa3 (g-verb:ar sa2 30 0.3 0.95)
        sa3 (freq-shift sa3 (* saa 6000))

        o (mix [sa sa2 (* 0.823 sa3)])
        o (wrap:ar o -0.05 0.35)
        oe (env-gen (perc 0 0.1 1 -5) 1 9000 1000 1)
        o (rlpf o oe 0.998)
        o (normalizer o)
        o (distort (* 100 o))
        me (env-gen (perc 0 0.14 1 -4) 1 1 0 1)
        o (* o me)

        x (detect-silence o 0.001 0.5 FREE)]
    (out 0 (pan2 o 0 1))))


(println "night --> custom synths defs loaded")

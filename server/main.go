package main

import (
	"fmt"
	"log"
	"net/http"
	"strings"
)

var actionCode string

func indexHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "Fighting ICE FDG 2020 Edition Server is ready.")
}

func updateHandler(w http.ResponseWriter, r *http.Request) {
	urlPath := r.URL.Path[1:]
	actionCode = strings.Replace(urlPath, "update/", "", -1)

	fmt.Fprintf(w, "Updated")
}

func getHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "%s", actionCode)
}

func main() {
	actionCode = "test"

	print("Started... Press Ctrl-C to terminate.")

	http.HandleFunc("/", indexHandler)
	http.HandleFunc("/update/", updateHandler)
	http.HandleFunc("/get/", getHandler)
	log.Fatal(http.ListenAndServe(":1688", nil))
}

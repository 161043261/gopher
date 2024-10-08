package utils

import "log/slog"

func FailOnErr(err error) {
	if err != nil {
		slog.Error(err.Error())
		panic(err)
	}
}

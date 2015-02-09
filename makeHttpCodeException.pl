#!/usr/bin/perl
use strict;

# add new exception names here:
my @exceptions = qw/ ... /;

foreach my $file (@exceptions) {

open (F, "> ./restapi/src/main/java/com/yandex/disk/rest/exceptions/http/$file.java") || die $!;

print F <<END;
package com.yandex.disk.rest.exceptions.http;

import com.yandex.disk.rest.json.ApiError;

public class $file extends HttpCodeException {
    public $file(int code, ApiError response) {
        super(code, response);
    }
}
END
close F;

}

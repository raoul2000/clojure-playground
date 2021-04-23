# metrics

Extract valuable information from a *metrics* log file

Log file are CSV formatted with following columns : 

- **timestamp**: date in ISO-8601 format when the task that produced this record was executed
- **taskId**: the Id of the task this record is describing
- **latestDownloadCount**: downloaded documents during the task execution that produced this record
- **execCount**: (cumulative) total execution count for this task when this record was created (see *timestamp*)
- **latestExecDurationMs**: execution duration for the task at the time this record was created
- **totalExecDurationMs**: (cumulative) the total time used to execute this task since it was started
- **avgExecDurationMs**: (cumulative) the average execution duration for this task since it was started
- **downloadTotalCount**: (cumulative) the total count of downloaded documents performed by this tasks since it was started

Example : 
```
2021-04-02T09:36:54.222Z,news,0,1,2364,2364,2364,0
2021-04-02T09:36:59.179Z,photos,2,1,7319,7319,7319,2
2021-04-02T09:37:14.596Z,news,0,2,372,2736,1368,0
2021-04-02T09:37:24.616Z,photos,3,2,5436,12755,6377,5
etc...
```

## Installation

Download from http://example.com/FIXME.

## Usage

FIXME: explanation

    $ java -jar metrics-0.1.0-standalone.jar [args]

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2021 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.

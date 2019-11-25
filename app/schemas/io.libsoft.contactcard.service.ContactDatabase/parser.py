import json
import re
import sys
from optparse import OptionParser

TO_BE_REPLACED = "${TABLE_NAME}"
END_LINE = ";"

parser = OptionParser()
parser.add_option("-f", "--file", dest="infile", help="Input Json file.")
parser.add_option("-o", "--output", dest="outfile",
                  help="Parsed output file.")
parser.add_option("-v", "--verbose", dest="sout",
                  help="Send output to console.", action="store_true",
                  default=False)

if __name__ == '__main__':

    (options, rem) = parser.parse_args()
    if options.infile is None and len(rem) == 0:
        sys.exit("Error: input file required.")

    input_file = options.infile if options.infile is not None else rem[0]
    outfile = options.outfile if options.outfile is not None else "ddl.sql"
    try:
        with open(input_file, 'r') as fp:
            json_input = json.load(fp)

    except json.JSONDecodeError:
        sys.exit("Valid Json format required.")
    except FileNotFoundError:
        sys.exit(f"File {input_file} not found.")

    sql_commands = []

    entities = json_input['database']['entities']
    for entity in entities:
        table_name = entity["tableName"]
        sql_commands.append(
            entity["createSql"].replace(TO_BE_REPLACED, table_name) + END_LINE)
        for index in entity['indices']:
            sql_commands.append(
                index['createSql'].replace(TO_BE_REPLACED,
                                           table_name) + END_LINE)

    if options.sout:
        for command in sql_commands:
            print(command)
    else:
        with open(outfile, 'w') as fp:
            for command in sql_commands:
                fp.write(command+"\n")


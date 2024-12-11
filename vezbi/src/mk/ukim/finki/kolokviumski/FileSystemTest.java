package mk.ukim.finki.kolokviumski;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


interface IFile{
     String getFileName();
     long getFileSize();
     String getFileInfo(int indent);
     void sortBySize();
     long findLargestFile ();
}

class FileNameExistsException extends Exception{
    public FileNameExistsException(String message){
        super(message);
    }
}

class File implements IFile{

    public String name;
    public long size;

    public File(String name, long size) {
        this.name = name;
        this.size = size;
    }


    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public long getFileSize() {
        return size;
    }

    @Override
    public String getFileInfo(int indent) {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<indent;i++)
        {
            sb.append("    ");
        }
       sb.append(String.format("File name:%10s File size:%10d\n", name, size));
        return sb.toString();
    }

    @Override
    public void sortBySize() {

    }

    @Override
    public long findLargestFile() {
        return 0;
    }
}

class Folder implements IFile{

    public String name;
    public long size;
    public List<IFile> files;

    public Folder(String name) {
        this.name = name;
        this.size = 0;
        this.files = new ArrayList<>();
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public long getFileSize() {
        for (IFile file : files) {

            if(file instanceof Folder)
            {
                size+=((Folder)file).getFileSize();
            }
            size+=file.getFileSize();
        }
        return size;
    }

    @Override
    public String getFileInfo(int indent) {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<indent;i++)
        {
            sb.append("    ");
        }
        sb.append(String.format("Folder name:%10s Folder size:%10d\n", name, size));
        files.forEach(iFile -> sb.append(iFile.getFileInfo(indent+1)));
        return sb.toString();
    }

    @Override
    public void sortBySize() {
        files.forEach(IFile::sortBySize);
        files.sort(Comparator.comparing(IFile::getFileSize));
    }

    @Override
    public long findLargestFile() {

        long max=0;
        for (IFile file : files) {
            long temp=0;
            if(file instanceof Folder)
            {
                temp=((Folder)file).findLargestFile();
            }else{
                temp=file.getFileSize();
            }
            if(temp>max)
            {
                max=temp;
            }
        }
//         List<IFile> sorted= files.stream().sorted(Comparator.comparing(IFile::getFileSize)).collect(Collectors.toList());
//         return sorted.get(sorted.size()-1).getFileSize();
        return max;


    }
    public void addFile (IFile file) throws FileNameExistsException {
        files.add(file);
    }
}


class FileSystem{
    public Folder rootDirectory;
    public FileSystem() {
        rootDirectory = new Folder("root");
    }
    public void addFile (IFile file) throws FileNameExistsException {
        for (IFile iFile : rootDirectory.files) {
            if(iFile instanceof Folder)
            {
                for (IFile file1 : ((Folder) iFile).files) {
                    if(file1.getFileName().equals(file.getFileName()))
                    {
                        throw new FileNameExistsException(String.format("There is already a file %s text in the folder %s", file.getFileName(), file1.getFileName()));
                    }
                }
            }else{
                if(iFile.getFileName().equals(file.getFileName()))
                {
                    throw new FileNameExistsException(String.format("There is already a file %s text in the folder %s", file.getFileName(), iFile.getFileName()));
                }
            }
        }
        rootDirectory.addFile(file);
    }
    public long findLargestFile (){
        return rootDirectory.findLargestFile();
    }
    public void sortBySize(){
        rootDirectory.files.forEach(IFile::sortBySize);
        rootDirectory.sortBySize();
    }

    @Override
    public String toString() {
        return rootDirectory.getFileInfo(0);
    }
}

public class FileSystemTest {

    public static Folder readFolder (Scanner sc)  {

        Folder folder = new Folder(sc.nextLine());
        int totalFiles = Integer.parseInt(sc.nextLine());

        for (int i=0;i<totalFiles;i++) {
            String line = sc.nextLine();

            if (line.startsWith("0")) {
                String fileInfo = sc.nextLine();
                String [] parts = fileInfo.split("\\s+");
                try {
                    folder.addFile(new File(parts[0], Long.parseLong(parts[1])));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
            else {
                try {
                    folder.addFile(readFolder(sc));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return folder;
    }

    public static void main(String[] args)  {

        //file reading from input

        Scanner sc = new Scanner (System.in);

        System.out.println("===READING FILES FROM INPUT===");
        FileSystem fileSystem = new FileSystem();
        try {
            fileSystem.addFile(readFolder(sc));
        } catch (FileNameExistsException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("===PRINTING FILE SYSTEM INFO===");
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING FILE SYSTEM INFO AFTER SORTING===");
        fileSystem.sortBySize();
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING THE SIZE OF THE LARGEST FILE IN THE FILE SYSTEM===");
        System.out.println(fileSystem.findLargestFile());




    }
}
package mk.ukim.finki.kolokviumski2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


class Comment{
    public String commentId;
    public String commentText;
    public String author;
    public int numLikes;

    public List<Comment> replies;

    public Comment(String commentId, String author ,String commentText) {
        this.commentId = commentId;
        this.commentText = commentText;
        this.numLikes = 0;
        this.author = author;
        this.replies = new ArrayList<>();
    }

    public void addLike()
    {
        numLikes++;
    }
    public void addReply(Comment comment)
    {
        replies.add(comment);
    }
    public int getTotalLikes()
    {
        int totalLikes = numLikes;
        for(Comment comment : replies)
        {
            totalLikes += comment.getTotalLikes();
        }
        return totalLikes;
    }

    public String toStringRecursive(int indentLevel)
    {
        StringBuilder sb = new StringBuilder();
       // replies.sort((c1, c2) -> Integer.compare(c2.getTotalLikes(), c1.getTotalLikes()));
        replies.sort(Comparator.comparing(Comment::getTotalLikes).reversed());
        String indent=" ".repeat(indentLevel);
        sb.append(indent);
        sb.append("Comment: ").append(commentText).append("\n");
        sb.append(indent);
        sb.append("Written by: ").append(author).append("\n");
        sb.append(indent);
        sb.append("Likes: ").append(numLikes).append("\n");
        for (Comment reply : replies) {
            sb.append(reply.toStringRecursive(indentLevel+4));
        }
        return sb.toString();
    }

    public int getNumLikes() {
        return numLikes;
    }
}

class Post{
    public String username;
    public String postContent;

    public Map<String, Comment> map;
    public List<Comment> topLevelComments;

    public Post(String username, String postContent) {
        this.username = username;
        this.postContent = postContent;
        this.map=new HashMap<>();
        this.topLevelComments=new ArrayList<>();
    }
    public void addComment (String username, String commentId, String content, String replyToId){
        Comment comment = new Comment(commentId, username, content);
        if(replyToId==null)
        {
            topLevelComments.add(comment);
            map.put(commentId,comment);
        }else{
            map.get(replyToId).addReply(comment);
            map.put(commentId,comment);
        }
    }
    public void likeComment (String commentId){
        Comment c = map.get(commentId);
        c.addLike();
    }

    @Override
    public String toString() {
        topLevelComments.sort(Comparator.comparing(Comment::getTotalLikes).reversed());
        StringBuilder sb = new StringBuilder();
        sb.append("Post: ").append(postContent).append("\n");
        sb.append("Written by: ").append(username).append("\n");
        sb.append("Comments: ").append("\n");
        for (Comment comment : topLevelComments) {
            sb.append(comment.toStringRecursive(8));
        }
        return sb.toString();
    }
}

public class PostTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String postAuthor = sc.nextLine();
        String postContent = sc.nextLine();

        Post p = new Post(postAuthor, postContent);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(";");
            String testCase = parts[0];

            if (testCase.equals("addComment")) {
                String author = parts[1];
                String id = parts[2];
                String content = parts[3];
                String replyToId = null;
                if (parts.length == 5) {
                    replyToId = parts[4];
                }
                p.addComment(author, id, content, replyToId);
            } else if (testCase.equals("likes")) { //likes;1;2;3;4;1;1;1;1;1 example
                for (int i = 1; i < parts.length; i++) {
                    p.likeComment(parts[i]);
                }
            } else {
                System.out.println(p);
            }

        }
    }
}
